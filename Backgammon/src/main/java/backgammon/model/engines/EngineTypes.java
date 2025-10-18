package backgammon.model.engines;

import backgammon.model.engines.randomMove.RandomMoveEngine;
import backgammon.model.engines.ruleBased.RuleBasedEngine;

public enum EngineTypes {
	PLAYER{
		@Override
		public Engine createEngine(){
			return null;
		}
	}, 
	RANDOM_MOVE_ENGINE{
		@Override
		public Engine createEngine(){
			return new RandomMoveEngine();
		}
	}, 
	RULE_BASED_ENGINE{
		@Override
		public Engine createEngine(){
			return new RuleBasedEngine();
		}
	};

	public abstract Engine createEngine();
}
