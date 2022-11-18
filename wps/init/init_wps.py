#!/usr/bin/env python3

import pathlib
import shutil

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




def main():
    CollectJsonFilesTask().run()


if __name__ == "__main__":
    main()
