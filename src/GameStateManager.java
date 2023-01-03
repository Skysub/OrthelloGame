import java.util.HashMap;

public class GameStateManager {

	GameState currentGameState;
	HashMap<String, GameState> gameStates;

	GameStateManager() {
		currentGameState = null;
		gameStates = new HashMap<String, GameState>();
	}

	  //Tilføjer en gameState til listen
	  public void AddGameState(String name, GameState state)
	  {
	    gameStates.put(name, state);   // gamestat tilføjes via string som Key, hvor state er værdien
	  }

	  //Ændrer hvilken gamestate der er aktiv
	  public void ChangeGameState(String name) {
	    if (currentGameState != null) currentGameState.Reset();
	    if (gameStates.containsKey(name))
	    {
	      currentGameState = gameStates.get(name);
	      currentGameState.OnEnter();
	    } else {
	    	System.out.println("'"+name+"' er ikke en gyldig gameState");
	    }
	  }

	  public void Reset()
	  {
	    if (currentGameState != null)
	      currentGameState.Reset();
	  }

	  //Skaffer selve gameStaten
	  public GameState GetGameState(String name)
	  {
	    if (gameStates.containsKey(name))
	      return gameStates.get(name);
	    return null;
	  }

	  //Skaffer navnet på den aktive gamestate
	  public String GetCurrentGameStateName() {
	    java.util.Set<String> kSet = gameStates.keySet();
	    for (String x : kSet) {
	      if ( GetGameState(x) == currentGameState ) {
	        return x;
	      }
	    }
	    return "";
	  }
}
