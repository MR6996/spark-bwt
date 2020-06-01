package com.randazzo.mario.sparkbwt.io;

import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.*;

import java.io.IOException;

public class BWTInputFormat extends FileInputFormat<NullWritable, ByteWritable> {

    @Override
    public RecordReader<NullWritable, ByteWritable> getRecordReader(InputSplit split, JobConf job, Reporter reporter)
            throws IOException {

        reporter.setStatus(split.toString());

        return new ByteRecordReader(job, (FileSplit) split);
    }

}
