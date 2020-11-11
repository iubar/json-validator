package it.iubar.json.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;

public class MyProblemHandler implements ProblemHandler {

	private static final Logger LOGGER = Logger.getLogger(MyProblemHandler.class.getName());

	private List<Problem> problems = null;
	private List<String> problems2 = new ArrayList<>();

	public List<Problem> getProblems() {
		return this.problems;
	}

	public List<String> getProblems2() {
		return this.problems2;
	}

	@Override
	public void handleProblems(List<Problem> problems) {
		this.problems = problems;
		for (Problem problem : problems) {
			this.problems2.add(problem.toString());
			MyProblemHandler.LOGGER.warning(problem.toString());
		}
	}

}
