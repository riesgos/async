import dataclasses
import pathlib
import sqlite3

import pytest


def quote(value):
    if isinstance(value, str):
        return f"'{value}'"
    return str(value)


@dataclasses.dataclass
class LiteralInput:
    id: int
    job_id: int
    wps_identifier: str
    input_value: str


class DatamanagementRepo:
    def __init__(self, db_connection):
        self.db_connection = db_connection

    def find_literal_inputs_of_parent_process(
        self, complex_output_link, wps_process_identifier
    ):
        sql = f"""
            select modelprop_inputs.id, modelprop_inputs.job_id, modelprop_inputs.wps_identifier, modelprop_inputs.input_value
            from literal_inputs modelprop_inputs
            join jobs modelprop_job on modelprop_job.id = modelprop_inputs.job_id
            join processes modelprop_process on modelprop_process.id = modelprop_job.process_id
            join complex_outputs modelprop_outputs on modelprop_outputs.job_id = modelprop_job.id
            join complex_outputs_as_inputs deus_inputs on deus_inputs.complex_output_id = modelprop_outputs.id
            join jobs deus_job on deus_job.id = deus_inputs.job_id
            join complex_outputs deus_outputs on deus_outputs.job_id = deus_job.id
            where deus_outputs.link='{complex_output_link}'
            and modelprop_process.wps_identifier='{wps_process_identifier}'
        """
        result = []
        for row in self.db_connection.execute(sql):
            result.append(
                LiteralInput(
                    id=row[0],
                    job_id=row[1],
                    wps_identifier=row[2],
                    input_value=row[3],
                )
            )
        return result


class Expectation:
    def __init__(self, value):
        self.value = value

    def to_equal(self, expected_value):
        assert self.value == expected_value


def expect(value):
    return Expectation(value)


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


def test_has_db_with_structure(db_connection):
    db_connection.execute("select * from literal_inputs")


def test_has_still_db_with_structure(db_connection):
    db_connection.execute("select * from literal_inputs")


def test_extract_schema_for_ts_output(db_connection):
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
                id=2, wps_identifier="schema", input_value="SUPPASRI2013_v2.0", job_id=2
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
                id=3, wps_identifier="schema", input_value="SARA_v1.0", job_id=3
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
                id=4, wps_identifier="schema", input_value="SARA_v1.0", job_id=4
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
                wps_identifier="",
                mime_type="application/json",
                encoding="UTF-8",
                xmlschema="",
                job_id=4,
                link="https://rz-vm140.gfz-potsdam.de/wps/results/deus/4",
            ),
        ),
    ]
    for insert in inserts:
        table = insert["table"]
        fields = ",".join(insert["values"].keys())
        values = ",".join(quote(v) for v in insert["values"].values())
        db_connection.execute(f"insert into {table}({fields}) values ({values})")
    # Ok, now we have the situation that I need to find the schema
    # with that the first modelprop was called.
    datamangement_repo = DatamanagementRepo(db_connection)

    deus_output_link = "https://rz-vm140.gfz-potsdam.de/wps/results/deus/4"
    modelprop_wps_process_identifier = "org.n52.gfz.riesgos.algorithm.ModelpropProcess"
    literal_inputs = datamangement_repo.find_literal_inputs_of_parent_process(
        deus_output_link, modelprop_wps_process_identifier
    )
    labels = [x.input_value for x in literal_inputs if x.wps_identifier == "schema"]
    assert len(labels) == 1
    assert labels[0] == "HAZUS"
