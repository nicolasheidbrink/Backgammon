package backgammon.model.engines;

import backgammon.model.engines.neuralNetwork.NeuralNetworkEngine;
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
	},
	NEURAL_NETWORK_ENGINE{
		@Override
		public Engine createEngine(){
			return new NeuralNetworkEngine();
		}
	};

	public abstract Engine createEngine();
}
