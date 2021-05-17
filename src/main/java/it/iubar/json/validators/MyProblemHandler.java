package it.iubar.json.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;

public class MyProblemHandler implements ProblemHandler {

	private static final Logger LOGGER = Logger.getLogger(MyProblemHandler.class.getName());
	 
	private List<Problem> problems = new ArrayList<>();
 
	public List<Problem> getProblems() {
		return this.problems;
	}

	@Override
	public void handleProblems(List<Problem> problems) {
		// non posso usare this.problems = problems perchè List è mutabile
		// e mi ritroverei con la lista vuota
 		for (Problem problem : problems) {
			this.problems.add(problem);
			MyProblemHandler.LOGGER.warning(problem.toString());
		}
	}

	public int countErrors() {
		return this.problems.size();
	}

}
