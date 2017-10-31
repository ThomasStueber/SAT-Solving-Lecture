import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Parser {

	
	public static Integer[][] parseFile(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = "";
		int numVars = 0;
		int numClauses = 0;
		boolean problemLineRead = false;
		int clauseCounter = 1;
		
		Integer[][] formula = null;
		ArrayList<Integer> currentClause = new ArrayList<Integer>();
		
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.charAt(0) == 'p') {
				String[] parts = line.split("[\t ]");
				if (parts.length != 4 || !parts[1].equals("cnf") || problemLineRead) {
					br.close();
					throw(new IOException());
				}
				
				try {
					numVars = Integer.parseInt(parts[2]);
					numClauses = Integer.parseInt(parts[3]);
				} catch(NumberFormatException e) {
					br.close();
					throw(new IOException());
				}
				formula = new Integer[numClauses+1][];
				formula[0] = new Integer[2];
				formula[0][0] = numVars;
				formula[0][1] = numClauses;
				problemLineRead = true;
			} else if (line.charAt(0) == 'c') {
				continue;
			} else if (problemLineRead) {
				String[] parts = line.split("[\t ]");
				for (int i = 0;i < parts.length;i++) {
					if (!parts[i].equals("0")) {
						try {
							currentClause.add(Integer.parseInt(parts[i]));
						} catch(NumberFormatException e) {
							System.out.println(parts[i]);
							br.close();
							throw(new IOException());
						}
					} else {
						Integer[] clause = new Integer[currentClause.size()];
						currentClause.toArray(clause);
						formula[clauseCounter] = clause;
						currentClause.clear();
						clauseCounter++;
					}
				}
			} else {
				br.close();
				throw(new IOException());
			}
			
		}
		
		br.close();
		
		return formula;
	}
	
	public static void printStatistics(String filename) throws IOException {
		Integer[][] formula = parseFile(filename);
		HashMap<Integer, Integer> occurencesVar = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> occurencesLiteral = new HashMap<Integer, Integer>();
		HashSet<Integer> vars = new HashSet<Integer>();
		HashSet<Integer> literals = new HashSet<Integer>();
		int clauseCount = 0;
		ArrayList<Integer> unitClauses = new ArrayList<Integer>();
		
		for (int i = 1;i < formula.length;i++) {
			for (int j = 0;j < formula[i].length;j++) {
				if (!occurencesVar.containsKey(Math.abs(formula[i][j]))) {
					occurencesVar.put(Math.abs(formula[i][j]), 0);
				}
				if (!occurencesLiteral.containsKey(formula[i][j])) {
					occurencesLiteral.put(formula[i][j], 0);
				}
				occurencesVar.put(Math.abs(formula[i][j]), occurencesVar.get(Math.abs(formula[i][j])) + 1);
				occurencesLiteral.put(formula[i][j], occurencesLiteral.get(formula[i][j]) + 1);
				vars.add(Math.abs(formula[i][j]));
				literals.add(formula[i][j]);
			}
			clauseCount++;
			if (formula[i].length == 1) {
				unitClauses.add(i-1);
			}
		}
		
		System.out.println("File: " + filename);
		System.out.println("Problem line: #vars = " + formula[0][0] + ", #clauses = " + formula[0][1]);
		System.out.println("Variable count: " + vars.size());
		System.out.println("Clause count: " + clauseCount);
		System.out.println("Literal count: " + literals.size());
		
		int max = 0;
		HashSet<Integer> maxVars = new HashSet<Integer>();
		for (int var : occurencesVar.keySet()) {
			if (occurencesVar.get(var) >= max) {
				if (occurencesVar.get(var) > max) {
					maxVars.clear();
				}
				max = occurencesVar.get(var);
				
				maxVars.add(var);
			}
		}
		System.out.println("Maximal occurrence of a variable: " + max);
		System.out.println("Variables with maximum number of occurrences: " + maxVars.toString());
		
		HashSet<Integer> purePositive = new HashSet<Integer>();
		HashSet<Integer> pureNegative = new HashSet<Integer>();
		for (int var : occurencesLiteral.keySet()) {
			if (occurencesLiteral.get(var) == 1) {
				if (var > 0) {
					purePositive.add(var);
				} else if (var < 0) {
					pureNegative.add(var);
				}
			}
		}
		
		System.out.println("Positive pure literals: " + purePositive.toString());
		System.out.println("Negative pure literals: " + pureNegative.toString());
		System.out.println("Unit clauses: " + unitClauses.toString());
	}
	
	public static void printFormula(Integer[][] formula) {
		for (int i = 1;i < formula.length;i++) {
			for (int j = 0;j < formula[i].length;j++) {
				System.out.print(formula[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
	
	
	public static void main(String args[]) {
		try {
			printStatistics("goldb-heqc-k2mul.cnf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
