package hadoop.leftjoin;

import hadoop.top10.Tuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class LeftJoinDriver extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res= ToolRunner.run(new Configuration(),new LeftJoinDriver(),args);
        System.exit(res);
    }

    static String preffix="/usr/local/Cellar/hadoop/test/";

    public int run(String[] args) throws Exception {
        Configuration conf=getConf();
        conf.set("mapred.job.tracker", "local");
        conf.set("fs.default.name", "local");

        Job job=new Job(conf);

        MultipleInputs.addInputPath(job,new Path(preffix+args[0]), TextInputFormat.class, LeftJoin1Mapper.class);
        MultipleInputs.addInputPath(job,new Path(preffix+args[1]), TextInputFormat.class, LeftJoin2Mapper.class);
        FileOutputFormat.setOutputPath(job,new Path(preffix+args[2]));

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Tuple.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setReducerClass(LeftJoinReducer.class);
//        job.setNumReduceTasks(4);
        return job.waitForCompletion(true)?1:0;
    }
}
