package use_case.blockblast;

public interface PlacePieceOutputBoundary {
    void prepareSuccessView(PlacePieceResponseModel responseModel);
    void prepareFailView(String message);
}
