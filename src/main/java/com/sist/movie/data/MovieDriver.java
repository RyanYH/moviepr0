package com.sist.movie.data;
import java.io.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MovieDriver {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf=new Configuration();
		Job job=new Job(conf, "MovieCount");
		
		job.setJarByClass(MovieDriver.class);
		job.setMapperClass(MovieMapper.class);
		job.setReducerClass(MovieReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path("/home/sist/bigdataStudy/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/MovieProject/desc.txt"));
		FileOutputFormat.setOutputPath(job, new Path("./output"));
		
		// Run
		job.waitForCompletion(true);
	}

}
