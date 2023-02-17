#!/usr/bin/env python3

import functools
import os
import pathlib
import re
import shutil
import time

from xml.etree import ElementTree
import requests


class Env:
    def str(self, key, default):
        return os.environ.get(key, default)


class CollectJsonFilesTask:
    processes_dir = pathlib.Path("/wps/processes")
    config_output_dir = pathlib.Path("/wps/json-configurations")

    def run(self):
        # We have one subfolder for each process (like shakyground).
        # And inside of those subfolders we have the json configs.
        for json_file in self.processes_dir.glob("*/*.json"):
            json_file_name = json_file.name
            output_file = self.config_output_dir / json_file_name
            shutil.copy(json_file, output_file)


class SetConnectionTimoutForServerXml:
    def run(self):

        filepath = "/tomcat/conf/server.xml"

        et = ElementTree.parse(filepath)
        server = et.getroot()
        service = server.find("Service")
        connector = service.find("Connector")
        connector.attrib["connectionTimeout"] = "180000"

        et.write(filepath)

class TriggerReloadOfWps:
    def run(self):
        # According to https://stackoverflow.com/a/32367339
        # It is enought to touch the web.xml to trigger a
        # restart of the webapp.
        filepath = "/tomcat/webapps/wps/WEB-INF/web.xml"
        os.system(f"touch {filepath}")


class ReplaceHostAndPortInWpsData:
    def get_csrf(self, resp):
        resp_elements = resp.text.split()
        csrf_idx = [i for i, x in enumerate(resp_elements) if x == 'name="_csrf"'][0]
        crsf_value_str = resp_elements[csrf_idx + 1]
        csrf_token = re.search('"(.*?)"', crsf_value_str).groups()[0]
        return csrf_token

    def wait_for(self, url):
        ready = False
        while not ready:
            try:
                resp = requests.get(url)
                resp.raise_for_status()
                ready = True
            except Exception:
                time.sleep(2)

    def set_hostname_and_hostport(
        self, base_url, hostname, hostport, initial_username, initial_password
    ):
        s = requests.Session() 
        resp = s.get(f"{base_url}/login")
        resp.raise_for_status()
        csrf_token = self.get_csrf(resp)
        resp = s.post(
            f"{base_url}/j_spring_security_check",
            {
                "username": initial_username,
                "password": initial_password,
                "_csrf": csrf_token,
            },
        )
        resp.raise_for_status()
        resp = s.get(f"{base_url}/server")
        resp.raise_for_status()
        csrf_token = self.get_csrf(resp)

        data = {
            "protocol": "http",
            "hostname": hostname,
            "hostport": hostport,
            "computation_timeout": "5",
            "weppapp_path": "wps",
            "repo_reload_interval": "0.0",
            "data_inputs_in_response": "false",
            "cache_capabilites": "false",
            "response_url_filter_enabled": "false",
            "min_pool_size": "10",
            "max_pool_size": "20",
            "keep_alive_seconds": "1000",
            "max_queued_tasks": "100",
            "max_request_size": "128",
            "add_process_description_Link_to_process_summary": "true",
        }

        # prepare the payload
        payload = []
        for key, value in data.items():
            payload.append(f"value={value}")
            payload.append(f"key={key}")
            payload.append("module=org.n52.wps.webapp.entities.Server")
        payload = "&".join(payload)

        resp = s.post(
            f"{base_url}/server",
            payload,
            headers={
                "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                "X-CSRF-TOKEN": csrf_token,
            },
        )
        resp.raise_for_status()

    def run(self):
        base_url = "http://riesgos-wps:8080/wps"

        self.wait_for(base_url)

        initial_username = "wps"
        initial_password = "wps"

        env = Env()
        hostname = env.str("WPS_HOSTNAME", default="riesgos-wps")
        hostport = env.str("WPS_HOSTPORT", default="8080")

        self.set_hostname_and_hostport(
            base_url=base_url,
            hostname=hostname,
            hostport=hostport,
            initial_username=initial_username,
            initial_password=initial_password,
        )


def main():
    SetConnectionTimoutForServerXml().run()
    print("Updated server.xml")
    CollectJsonFilesTask().run()
    print("Collected json configurations")
    ReplaceHostAndPortInWpsData().run()
    print("Updated hostname and port of wps")
    TriggerReloadOfWps().run()


if __name__ == "__main__":
    main()
