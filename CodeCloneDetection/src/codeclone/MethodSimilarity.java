package codeclone;

import java.util.HashMap;
import java.util.Map;

public class MethodSimilarity {

	public double simMethodPara, simMethodType;
	public double simTokenType, simTokenKeyword, simTokenOtherStr;
	public double simTokenMarker, simTokenOperator, simTokenOtherChar;
	public double simTokenNum;
	public double w1 = 0.1, w2 = 0.1, w3 = 0.1, w4 = 0.15, w5 = 0.3, w6 = 0.05, w7 = 0.1, w8 = 0.05, w9 = 0.05;
	public double methodSimilarity;
	
	//public double tokenThreshold = 0.7;
	public double detectThreshold = 0.5;
	
	public static int countID = 1;
	
	public String str1, str2;
	public TokenList tokenList1, tokenList2;
	
	// calculate the similarity of both TokenLists
	public double tokenListSim(TokenList tList1, TokenList tList2) {
		double tokenListDis = 0;
		Map<String, Integer> tVector1 = new HashMap<String, Integer>();
		Map<String, Integer> tVector2 = new HashMap<String, Integer>();
		
		// put list into map
		for(int index1 = 0; index1 < tList1.size(); index1++)
			tVector1.put(tList1.getTokenVector(index1).TokenName, tList1.getTokenVector(index1).TokenCount);
		for(int index2 = 0; index2 < tList2.size(); index2++)
			tVector2.put(tList2.getTokenVector(index2).TokenName, tList2.getTokenVector(index2).TokenCount);
		
		for (Map.Entry<String, Integer> entry1 : tVector1.entrySet()) {
			if(tVector2.containsKey(entry1.getKey()))
				// if list1 and list2 have the same tokenName, then calculate (tokenCount1-tokenCount2)^2
				tokenListDis += Math.pow((entry1.getValue() - tVector2.get(entry1.getKey())), 2);
			else
				// if list2 does not contain the tokenName of list1, then calculate (tokenCount1 - 0)^2
				tokenListDis += entry1.getValue() * entry1.getValue();
		}
		for (Map.Entry<String, Integer> entry2 : tVector2.entrySet()) {
			if(!tVector1.containsKey(entry2.getKey()))
				// if list1 does not contain the tokenName of list2, then calculate (tokenCount2 - 0)^2
				tokenListDis += entry2.getValue() * entry2.getValue();
		}
		return 1.0 / (1 + Math.sqrt(tokenListDis));
	}
	
	/*
	public void tokenListUnification() {
		BiGramSimilarity biGramSim = new BiGramSimilarity();
		for(int index1 = 0; index1 < tokenList1.size(); index1++) {
			for(int index2 = 0; index2 < tokenList2.size(); index2++) {
				double simTokenName = biGramSim.simScore(
						biGramSim.bigram(tokenList1.getTokenVector(index1).TokenName), 
						biGramSim.bigram(tokenList2.getTokenVector(index2).TokenName));
				if(simTokenName > tokenThreshold)
					tokenList2.getTokenVector(index2).TokenName = tokenList1.getTokenVector(index1).TokenName;
			}
		}
	}
	*/
	
	public double methodVectorSim(MethodVector mVector1, MethodVector mVector2) {
		// calculate methodPara's similarity 
		BiGramSimilarity biGramSim = new BiGramSimilarity();
		str1 = mVector1.methodPara;
		str2 = mVector2.methodPara;
		simMethodPara = biGramSim.simScore(biGramSim.bigram(str1), biGramSim.bigram(str2));
		
		// calculate methodType's similarity 
		str1 = mVector1.methodType;
		str2 = mVector2.methodType;
		if(str1.equals(str2))
			simMethodType = 1;
		else
			simMethodType = 0;
		
		// calculate token_Type's similarity 
		tokenList1 = mVector1.methodTokenList.getListByType("Type");
		tokenList2 = mVector2.methodTokenList.getListByType("Type");
		simTokenType = tokenListSim(tokenList1, tokenList2);
		
		// calculate token_Keyword's similarity 
		tokenList1 = mVector1.methodTokenList.getListByType("Keyword");
		tokenList2 = mVector2.methodTokenList.getListByType("Keyword");
		simTokenKeyword = tokenListSim(tokenList1, tokenList2);
		
		// calculate token_OtherStr's similarity 
		tokenList1 = mVector1.methodTokenList.getListByType("OtherStr");
		tokenList2 = mVector2.methodTokenList.getListByType("OtherStr");
		/*
		 * tokenListUnification();
		 */
		simTokenOtherStr = tokenListSim(tokenList1, tokenList2);
		
		// calculate token_Marker's similarity 
		tokenList1 = mVector1.methodTokenList.getListByType("Marker");
		tokenList2 = mVector2.methodTokenList.getListByType("Marker");
		simTokenMarker = tokenListSim(tokenList1, tokenList2);
		
		// calculate token_Operator's similarity 
		tokenList1 = mVector1.methodTokenList.getListByType("Operator");
		tokenList2 = mVector2.methodTokenList.getListByType("Operator");
		simTokenOperator = tokenListSim(tokenList1, tokenList2);
		
		// calculate token_OtherChar's similarity 
		tokenList1 = mVector1.methodTokenList.getListByType("OtherChar");
		tokenList2 = mVector2.methodTokenList.getListByType("OtherChar");
		simTokenOtherChar = tokenListSim(tokenList1, tokenList2);
		
		// calculate token_Num's similarity 
		tokenList1 = mVector1.methodTokenList.getListByType("Num");
		tokenList2 = mVector2.methodTokenList.getListByType("Num");
		simTokenNum = tokenListSim(tokenList1, tokenList2);
		
		/*
		System.out.printf("{%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f}", simMethodPara, simMethodType,
				simTokenType, simTokenKeyword, simTokenOtherStr, simTokenMarker, simTokenOperator, 
				simTokenOtherChar);
		System.out.println();
		*/
		// calculate the similarity between two methods
		return simMethodPara * w1 + simMethodType * w2 + simTokenType * w3 +
				simTokenKeyword * w4 + simTokenOtherStr * w5 + simTokenMarker * w6 +
				simTokenOperator * w7 + simTokenOtherChar * w8 + simTokenNum * w9;
	}
	
	// code clone detector for a single java file
	public void simDetector(MethodList mList) {
		for(int index1 = 0; index1 < mList.size() - 1; index1++) {
			for(int index2 = index1 + 1; index2 < mList.size(); index2++) {
				methodSimilarity = methodVectorSim(mList.getMethodVector(index1), mList.getMethodVector(index2));
				// output clone group
				if(methodSimilarity >= detectThreshold
						&& mList.getMethodVector(index1).endLineNumber - mList.getMethodVector(index1).startLineNumber > 7
						&& mList.getMethodVector(index2).endLineNumber - mList.getMethodVector(index2).startLineNumber > 7) {
					//System.out.println("1");
					System.out.println("Clone Group " + countID + " --> " +
							"Similarity :" + String.format("%.4f", methodSimilarity));
					System.out.printf("%15s%15d%15d\n", 
							mList.getMethodVector(index1).methodName,
							mList.getMethodVector(index1).startLineNumber,
							mList.getMethodVector(index1).endLineNumber);
					System.out.printf("%15s%15d%15d\n", 
							mList.getMethodVector(index2).methodName,
							mList.getMethodVector(index2).startLineNumber,
							mList.getMethodVector(index2).endLineNumber);
					countID++;
				}
				//else
				//	System.out.println(0);
			}
		}
	}
	
	// code clone detector for two java files
	public void simDetector(MethodList mList1, MethodList mList2) {
		for(int index1 = 0; index1 < mList1.size(); index1++) {
			for(int index2 = 0; index2 < mList2.size(); index2++) {
				methodSimilarity = methodVectorSim(mList1.getMethodVector(index1), mList2.getMethodVector(index2));
				// output clone group
				if(methodSimilarity >= detectThreshold
						&& mList1.getMethodVector(index1).endLineNumber - mList1.getMethodVector(index1).startLineNumber > 7
						&& mList2.getMethodVector(index2).endLineNumber - mList2.getMethodVector(index2).startLineNumber > 7) {
					System.out.println("Clone Group " + countID + " --> " +
							"Similarity :" + String.format("%.4f", methodSimilarity));
					System.out.printf("%15s%15d%15d\n", 
							mList1.getMethodVector(index1).methodName,
							mList1.getMethodVector(index1).startLineNumber,
							mList1.getMethodVector(index1).endLineNumber);
					System.out.printf("%15s%15d%15d\n", 
							mList2.getMethodVector(index2).methodName,
							mList2.getMethodVector(index2).startLineNumber,
							mList2.getMethodVector(index2).endLineNumber);
					countID++;
				}
			}
		}
	}
}
