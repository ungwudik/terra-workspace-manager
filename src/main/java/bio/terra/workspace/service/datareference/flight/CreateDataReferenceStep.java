package bio.terra.workspace.service.datareference.flight;

import bio.terra.stairway.FlightContext;
import bio.terra.stairway.FlightMap;
import bio.terra.stairway.Step;
import bio.terra.stairway.StepResult;
import bio.terra.stairway.exception.RetryException;
import bio.terra.workspace.common.utils.FlightUtils;
import bio.terra.workspace.db.DataReferenceDao;
import bio.terra.workspace.generated.model.CreateDataReferenceRequestBody;
import bio.terra.workspace.service.job.JobMapKeys;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;

public class CreateDataReferenceStep implements Step {

  private DataReferenceDao dataReferenceDao;

  public CreateDataReferenceStep(DataReferenceDao dataReferenceDao) {
    this.dataReferenceDao = dataReferenceDao;
  }

  @Override
  public StepResult doStep(FlightContext flightContext) throws RetryException {
    FlightMap inputMap = flightContext.getInputParameters();
    UUID referenceId = inputMap.get(DataReferenceFlightMapKeys.REFERENCE_ID, UUID.class);
    UUID workspaceId = inputMap.get(DataReferenceFlightMapKeys.WORKSPACE_ID, UUID.class);
    String reference = inputMap.get(DataReferenceFlightMapKeys.REFERENCE, String.class);
    CreateDataReferenceRequestBody body =
        inputMap.get(JobMapKeys.REQUEST.getKeyName(), CreateDataReferenceRequestBody.class);

    dataReferenceDao.createDataReference(
        referenceId,
        workspaceId,
        body.getName(),
        body.getResourceId(),
        body.getCredentialId(),
        body.getCloningInstructions(),
        body.getReferenceType(),
        JsonNullable.of(reference));

    FlightUtils.setResponse(flightContext, referenceId.toString(), HttpStatus.OK);

    return StepResult.getStepResultSuccess();
  }

  @Override
  public StepResult undoStep(FlightContext flightContext) {
    FlightMap inputMap = flightContext.getInputParameters();
    UUID workspaceId = inputMap.get(DataReferenceFlightMapKeys.REFERENCE_ID, UUID.class);
    dataReferenceDao.deleteDataReference(workspaceId);
    return StepResult.getStepResultSuccess();
  }
}
