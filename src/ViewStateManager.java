import java.util.HashMap;

public class ViewStateManager {

	ViewType currentView;
	ViewState currentViewState;
	ViewState[] viewStates;

	ViewStateManager() {
		currentViewState = null;
		currentView = null;
		viewStates = new ViewState[ViewType.values().length]; 
	}

	//Tilføjer en viewState til listen
	public void AddViewState(ViewType type, ViewState state) {
		viewStates[type.getIndex()] = state;
	}

	//Ændrer hvilken gamestate der er aktiv
	public void ChangeViewState(ViewType type) {

		if (currentViewState != null) currentViewState.Reset();
		
		for (int i = 0; i < viewStates.length; i++) {
			if (type.getIndex() == i) {
				if (viewStates[i] != null) {
					currentViewState = viewStates[i];
					currentViewState.OnEnter();
					currentView = type;
				}
				else {
					System.out.println("ViewType '" + type.toString() + "' is not initialized.");
				}
			}
		}
	  }

	public void Reset() {
		if (currentViewState != null) {
			currentViewState.Reset();
		}
	}

	//Skaffer selve viewStaten
	public ViewState GetViewState(ViewType type) {
		return viewStates[type.getIndex()];
	}

	//Skaffer navnet på den aktive gamestate
	public String GetCurrentViewStateName() {
		return currentView.toString();
	}
}
