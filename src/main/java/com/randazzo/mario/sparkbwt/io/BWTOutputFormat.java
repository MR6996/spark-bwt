package com.randazzo.mario.sparkbwt.io;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.DataOutputStream;
import java.io.IOException;

public class BWTOutputFormat extends FileOutputFormat<NullWritable, ByteWritable> {


    @Override
    public RecordWriter<NullWritable, ByteWritable> getRecordWriter(FileSystem ignored, JobConf job, String name, Progressable progress)
            throws IOException {
        boolean isCompressed = getCompressOutput(job);

        if (!isCompressed) {
            Path file = FileOutputFormat.getTaskOutputPath(job, name);
            FileSystem fs = file.getFileSystem(job);
            FSDataOutputStream fileOut = fs.create(file, progress);
            return new CharRecordWriter(fileOut);
        } else {
            Class<? extends CompressionCodec> codecClass = getOutputCompressorClass(job, GzipCodec.class);
            // create the named codec
            CompressionCodec codec = ReflectionUtils.newInstance(codecClass, job);
            // build the filename including the extension
            Path file = FileOutputFormat.getTaskOutputPath(job, name + codec.getDefaultExtension());
            FileSystem fs = file.getFileSystem(job);
            FSDataOutputStream fileOut = fs.create(file, progress);
            return new CharRecordWriter(new DataOutputStream(codec.createOutputStream(fileOut)));
        }
    }


    static class CharRecordWriter implements RecordWriter<NullWritable, ByteWritable> {

        DataOutputStream out;

        CharRecordWriter(DataOutputStream fileOut) {
            out = fileOut;
        }

        @Override
        public synchronized void write(NullWritable key, ByteWritable value) throws IOException {
            if (value == null) { return; }

            out.write(value.get());
        }

        @Override
        public synchronized void close(Reporter reporter) throws IOException {
            out.close();
        }
    }
}
