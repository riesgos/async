import dataclasses


@dataclasses.dataclass
class LiteralInput:
    id: int
    job_id: int
    wps_identifier: str
    input_value: str


@dataclasses.dataclass
class ComplexOutput:
    id: int
    job_id: int
    wps_identifier: str
    link: str
    mime_type: str
    xmlschema: str
    encoding: str


@dataclasses.dataclass
class ComplexOutputAsInput:
    id: int
    job_id: int
    wps_identifier: str
    complex_output: ComplexOutput


class ComplexOutputAsInputRepo:
    def __init__(self, db_connection):
        self.db_connection = db_connection

    def find_inputs_by_job_id(self, job_id):
        sql = f"""
            select
                complex_outputs_as_inputs.id,
                complex_outputs_as_inputs.job_id,
                complex_outputs_as_inputs.wps_identifier,
                complex_outputs_as_inputs.complex_output_id,
                complex_outputs.job_id as output_job_id,
                complex_outputs.wps_identifier as output_wps_identifier,
                complex_outputs.link as output_link,
                complex_outputs.mime_type as output_mime_type,
                complex_outputs.xmlschema as output_xmlschema,
                complex_outputs.encoding as output_encoding
            from complex_outputs_as_inputs
            join complex_outputs on complex_outputs.id = complex_outputs_as_inputs.complex_output_id
            where complex_outputs_as_inputs.job_id = {job_id}
        """
        result = []
        for row in self.db_connection.execute(sql):
            result.append(
                ComplexOutputAsInput(
                    id=row[0],
                    job_id=row[1],
                    wps_identifier=row[2],
                    complex_output=ComplexOutput(
                        id=row[3],
                        job_id=row[4],
                        wps_identifier=row[5],
                        link=row[6],
                        mime_type=row[7],
                        xmlschema=row[8],
                        encoding=row[9],
                    ),
                )
            )
        return result


class DatamanagementRepo:
    def __init__(self, db_connection):
        self.db_connection = db_connection

    @property
    def complex_output_as_input_repo(self):
        return ComplexOutputAsInputRepo(self.db_connection)

    def find_literal_inputs_for_complex_output(
        self, complex_output_link, wps_process_identifier, wps_input_identifier
    ):
        sql = f"""
            select literal_inputs.id, literal_inputs.job_id, literal_inputs.wps_identifier, literal_inputs.input_value
            from literal_inputs
            join jobs on jobs.id = literal_inputs.job_id
            join processes on processes.id = jobs.process_id
            join complex_outputs on complex_outputs.job_id = jobs.id
            where complex_outputs.link = '{complex_output_link}'
            and literal_inputs.wps_identifier = '{wps_input_identifier}'
            and processes.wps_identifier = '{wps_process_identifier}'
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

    def find_literal_inputs_for_parent_process_of_complex_output(
        self,
        wps_process_identifier,
        complex_output_link,
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
