package com.lc.nlp4han.ner.character;

import java.io.IOException;

import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.ner.NEREvaluateMonitor;
import com.lc.nlp4han.ner.NERMeasure;
import com.lc.nlp4han.ner.NERWordOrCharacterSample;

/**
 * 基于字的命名实体识别的交叉验证
 * @author 王馨苇
 *
 */
public class NERCharacterCrossValidation {

	private final TrainingParameters params;
	private NEREvaluateMonitor[] monitor;
	
	/**
	 * 构造
	 * @param languageCode 编码格式
	 * @param params 训练的参数
	 * @param listeners 监听器
	 */
	public NERCharacterCrossValidation(TrainingParameters params,NEREvaluateMonitor... monitor){
		this.params = params;
		this.monitor = monitor;
	}
	
	/**
	 * 交叉验证十折评估
	 * @param sample 样本流
	 * @param nFolds 折数
	 * @param contextGenerator 上下文
	 * @throws IOException io异常
	 */
	public void evaluate(ObjectStream<NERWordOrCharacterSample> sample, int nFolds,
			NERCharacterContextGenerator contextGenerator) throws IOException{
		CrossValidationPartitioner<NERWordOrCharacterSample> partitioner = new CrossValidationPartitioner<NERWordOrCharacterSample>(sample, nFolds);
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			CrossValidationPartitioner.TrainingSampleStream<NERWordOrCharacterSample> trainingSampleStream = partitioner.next();
			ModelWrapper model = NERCharacterME.train(trainingSampleStream, params, contextGenerator);

			NERCharacterEvaluator evaluator = new NERCharacterEvaluator(new NERCharacterME(model, contextGenerator), monitor);
			NERMeasure measure = new NERMeasure();
			
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
//		System.out.println(measure);
	}
}
