package com.lc.nlp4han.constituent.unlex;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.pcfg.ConstituentParserCKYLoosePCNF;
import com.lc.nlp4han.constituent.pcfg.ConstituentTreeStream;
import com.lc.nlp4han.constituent.pcfg.PCFG;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;

public class EvalToolLatentAnnotation_foolish
{
	private static void usage()
	{
		System.out.println(EvalToolLatentAnnotation_foolish.class.getName() + "\n"
				+ "-train <trainFile> -gold <goldFile> [-smooth <smoothRate>] [-trainEncoding <trainEncoding>] [-goldEncoding <trainEncoding>] [-em <emIterations>]");
	}

	public static void eval(String trainF, String goldF, String trainEn, String goldEn, int iterations,
			double smoothRate, double pruneThreshold, boolean secondPrune, boolean prior) throws IOException
	{
		long start = System.currentTimeMillis();
		Grammar baseline = GrammarExtractorToolLatentAnnotation.getGrammar(0, 0, 0, smoothRate,
				Lexicon.DEFAULT_RAREWORD_THRESHOLD, trainF, trainEn);
		PCFG pcfg = baseline.getPCFG();
		Grammar gLatentAnntation = GrammarExtractorToolLatentAnnotation.getGrammar(1, 0.5, iterations, smoothRate,
				Lexicon.DEFAULT_RAREWORD_THRESHOLD, trainF, trainEn);

		long end = System.currentTimeMillis();
		ConstituentParserCKYLoosePCNF p2nf = new ConstituentParserCKYLoosePCNF(pcfg, pruneThreshold, secondPrune,
				prior);
		ConstituentParserLatentAnnotation parser = new ConstituentParserLatentAnnotation_foolish(p2nf,
				gLatentAnntation);
		EvaluatorLatentAnnotation_foolish evaluator = new EvaluatorLatentAnnotation_foolish(parser);
		ConstituentMeasure measure = new ConstituentMeasure();
		evaluator.setMeasure(measure);
		ObjectStream<String> treeStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(goldF)),
				goldEn);
		ObjectStream<ConstituentTree> sampleStream = new ConstituentTreeStream(treeStream);
		evaluator.evaluate(sampleStream);

		ConstituentMeasure measureRes = evaluator.getMeasure();
		System.out.println(end - start);
		System.out.println(measureRes);
	}

	public static void main(String[] args)
	{
		String trainFilePath = null;
		String goldFilePath = null;
		String trainEncoding = "utf-8";
		String goldEncoding = "utf-8";
		double smoothRate = 0.01;
		double pruneThreshold = 0.0001;
		boolean secondPrune = false;
		boolean prior = false;
		int iterations = 50;// em算法迭代次数
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-train"))
			{
				trainFilePath = args[i + 1];
				i++;
			}
			if (args[i].equals("-gold"))
			{
				goldFilePath = args[i + 1];
				i++;
			}
			if (args[i].equals("-trainEncoding"))
			{
				trainEncoding = args[i + 1];
				i++;
			}
			if (args[i].equals("-goldEncoding"))
			{
				goldEncoding = args[i + 1];
				i++;
			}
			if (args[i].equals("-em"))
			{
				iterations = Integer.parseInt(args[i + 1]);
				GrammarTrainer.EMIterations = iterations;
				i++;
			}
			if (args[i].equals("-pruneThreshold"))
			{
				pruneThreshold = Double.parseDouble(args[i + 1]);
				i++;
			}
			if (args[i].equals("-secondPrune"))
			{
				secondPrune = Boolean.getBoolean(args[i + 1]);
				i++;
			}
			if (args[i].equals("-prior"))
			{
				prior = Boolean.getBoolean(args[i + 1]);
				i++;
			}
			if (args[i].equals("-smooth"))
			{
				smoothRate = Double.parseDouble(args[i + 1]);
				i++;
			}
		}
		if (trainFilePath == null || goldFilePath == null)
		{
			usage();
			System.exit(0);
		}
		try
		{
			eval(trainFilePath, goldFilePath, trainEncoding, goldEncoding, iterations, smoothRate, pruneThreshold,
					secondPrune, prior);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
