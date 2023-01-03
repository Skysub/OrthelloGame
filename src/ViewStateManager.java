import java.util.HashMap;

public class ViewStateManager {

	ViewState currentViewState;
	HashMap<String, ViewState> viewStates;

	ViewStateManager() {
		currentViewState = null;
		viewStates = new HashMap<String, ViewState>();
	}

	  //Tilføjer en viewState til listen
	  public void AddViewState(String name, ViewState state)
	  {
	    viewStates.put(name, state);   // gamestat tilføjes via string som Key, hvor state er værdien
	  }

	  //Ændrer hvilken gamestate der er aktiv
	  public void ChangeViewState(String name) {
	    if (currentViewState != null) currentViewState.Reset();
	    if (viewStates.containsKey(name))
	    {
	      currentViewState = viewStates.get(name);
	      currentViewState.OnEnter();
	    } else {
	    	System.out.println("'"+name+"' er ikke en gyldig viewState");
	    }
	  }

	  public void Reset()
	  {
	    if (currentViewState != null)
	      currentViewState.Reset();
	  }

	  //Skaffer selve viewStaten
	  public ViewState GetViewState(String name)
	  {
	    if (viewStates.containsKey(name))
	      return viewStates.get(name);
	    return null;
	  }

	  //Skaffer navnet på den aktive gamestate
	  public String GetCurrentViewStateName() {
	    java.util.Set<String> kSet = viewStates.keySet();
	    for (String x : kSet) {
	      if ( GetViewState(x) == currentViewState ) {
	        return x;
	      }
	    }
	    return "";
	  }
}
