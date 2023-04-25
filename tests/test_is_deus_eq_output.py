import dataclasses
import pathlib
import sqlite3

import pytest

from datamanagement_repo import DatamanagementRepo


def is_deus_eq_output(db_connection, job_id):
    datamanagement_repo = DatamanagementRepo(db_connection)
    complex_outputs_of_other_processes_as_inputs_for_deus_output = (
        datamanagement_repo.complex_output_as_input_repo.find_inputs_by_job_id(job_id)
    )
    for input in complex_outputs_of_other_processes_as_inputs_for_deus_output:
        output = input.complex_output
        assetmaster_wps_process_identifier = (
            "org.n52.gfz.riesgos.algorithm.AssetmasterProcess"
        )
        assetmaster_wps_input_identifier = "schema"
        eq_schema_input_values = ["SARA_v1.0"]
        if all(
            [
                (x.input_value in eq_schema_input_values)
                for x in datamanagement_repo.find_literal_inputs_for_complex_output(
                    output.link,
                    assetmaster_wps_process_identifier,
                    assetmaster_wps_process_identifier,
                )
            ]
        ):
            return True

    return False


def quote(value):
    if isinstance(value, str):
        return f"'{value}'"
    return str(value)


@pytest.fixture
def connection():
    with sqlite3.connect(":memory:") as con:
        yield con


@pytest.fixture
def db_connection(connection):
    current_file = pathlib.Path(__file__)
    migrations_folder = current_file.parent.parent / "backend" / "migrations"
    migrations_files = sorted(migrations_folder.glob("V*.sql"))
    for m_file in migrations_files:
        commands_str = m_file.open().read()
        commands_list = commands_str.split(";")
        for command in commands_list:
            connection.execute(command)
    yield connection


def test_is_deus_eq_output(db_connection):
    # First create some example data
    inserts = [
        dict(
            table="processes",
            values=dict(
                id=1,
                wps_url="https://rz-vm140.gfz-potsdam.de",
                wps_identifier="org.n52.gfz.riesgos.algorithm.ModelpropProcess",
            ),
        ),
        dict(
            table="processes",
            values=dict(
                id=2,
                wps_url="https://rz-vm140.gfz-potsdam.de",
                wps_identifier="org.n52.gfz.riesgos.algorithm.AssetmasterProcess",
            ),
        ),
        dict(
            table="processes",
            values=dict(
                id=3,
                wps_url="https://rz-vm140.gfz-potsdam.de",
                wps_identifier="org.n52.gfz.riesgos.algorithm.DeusProcess",
            ),
        ),
        # First call of modelprop.
        dict(table="jobs", values=dict(id=1, process_id=1, status="success")),
        dict(
            table="literal_inputs",
            values=dict(id=1, wps_identifier="schema", input_value="HAZUS", job_id=1),
        ),
        dict(
            table="literal_inputs",
            values=dict(
                id=2, wps_identifier="assetcategory", input_value="buildings", job_id=1
            ),
        ),
        dict(
            table="complex_outputs",
            values=dict(
                id=1,
                wps_identifier="selectedRows",
                mime_type="application/json",
                encoding="UTF-8",
                xmlschema="",
                job_id=1,
                link="https://rz-vm140.gfz-potsdam.de/wps/results/modelprop/1",
            ),
        ),
        # Second call of modelprop.
        dict(table="jobs", values=dict(id=2, process_id=1, status="success")),
        dict(
            table="literal_inputs",
            values=dict(
                id=3, wps_identifier="schema", input_value="SUPPASRI2013_v2.0", job_id=2
            ),
        ),
        dict(
            table="literal_inputs",
            values=dict(
                id=4, wps_identifier="assetcategory", input_value="buildings", job_id=2
            ),
        ),
        dict(
            table="complex_outputs",
            values=dict(
                id=2,
                wps_identifier="selectedRows",
                mime_type="application/json",
                encoding="UTF-8",
                xmlschema="",
                job_id=2,
                link="https://rz-vm140.gfz-potsdam.de/wps/results/modelprop/2",
            ),
        ),
        # The call to assetmaster.
        dict(table="jobs", values=dict(id=3, process_id=2, status="success")),
        dict(
            table="literal_inputs",
            values=dict(
                id=5, wps_identifier="schema", input_value="SARA_v1.0", job_id=3
            ),
        ),
        dict(
            table="literal_inputs",
            values=dict(
                id=6, wps_identifier="querymode", input_value="intersects", job_id=3
            ),
        ),
        dict(
            table="complex_outputs",
            values=dict(
                id=3,
                wps_identifier="selectedRowsGeoJson",
                mime_type="application/json",
                encoding="UTF-8",
                xmlschema="",
                job_id=3,
                link="https://rz-vm140.gfz-potsdam.de/wps/results/assetmaster/3",
            ),
        ),
        # The eq deus
        dict(table="jobs", values=dict(id=4, process_id=3, status="success")),
        dict(
            table="literal_inputs",
            values=dict(
                id=7, wps_identifier="schema", input_value="SARA_v1.0", job_id=4
            ),
        ),
        dict(
            table="complex_outputs_as_inputs",
            values=dict(
                id=1,
                job_id=4,
                wps_identifier="exposure",
                complex_output_id=3,
            ),
        ),
        dict(
            table="complex_outputs_as_inputs",
            values=dict(
                id=2,
                job_id=4,
                wps_identifier="fragility",
                complex_output_id=1,
            ),
        ),
        dict(
            table="complex_outputs",
            values=dict(
                id=4,
                wps_identifier="merged_output",
                mime_type="application/json",
                encoding="UTF-8",
                xmlschema="",
                job_id=4,
                link="https://rz-vm140.gfz-potsdam.de/wps/results/deus/4",
            ),
        ),
        # And tsunami deus
        dict(table="jobs", values=dict(id=5, process_id=3, status="success")),
        dict(
            table="literal_inputs",
            values=dict(id=8, wps_identifier="schema", input_value="HAZUS", job_id=5),
        ),
        dict(
            table="complex_outputs_as_inputs",
            values=dict(
                id=3,
                job_id=5,
                wps_identifier="exposure",
                complex_output_id=4,
            ),
        ),
        dict(
            table="complex_outputs_as_inputs",
            values=dict(
                id=4,
                job_id=5,
                wps_identifier="fragility",
                complex_output_id=2,
            ),
        ),
        dict(
            table="complex_outputs",
            values=dict(
                id=5,
                wps_identifier="merged_output",
                mime_type="application/json",
                encoding="UTF-8",
                xmlschema="",
                job_id=5,
                link="https://rz-vm140.gfz-potsdam.de/wps/results/deus/5",
            ),
        ),
    ]
    for insert in inserts:
        table = insert["table"]
        fields = ",".join(insert["values"].keys())
        values = ",".join(quote(v) for v in insert["values"].values())
        db_connection.execute(f"insert into {table}({fields}) values ({values})")
    eq_deus_job_id = 4
    ts_deus_job_id = 5
    assert is_deus_eq_output(db_connection, eq_deus_job_id)
    assert is_deus_eq_output(db_connection, ts_deus_job_id)
