package backgammon.application.model.engines;

import backgammon.application.model.engines.neuralNetwork.NeuralNetworkEngine;
import backgammon.application.model.engines.randomMove.RandomMoveEngine;
import backgammon.application.model.engines.ruleBased.RuleBasedEngine;

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
	NEURAL_NETWORK_ENGINE_WITH_EXPLORATION{
		@Override
		public Engine createEngine(){
			return new NeuralNetworkEngine(true);
		}
	},
	NEURAL_NETWORK_ENGINE_WITHOUT_EXPLORATION{
		@Override
		public Engine createEngine(){
			return new NeuralNetworkEngine(false);
		}
	};

	public abstract Engine createEngine();
}
