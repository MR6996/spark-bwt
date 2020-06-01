package com.randazzo.mario.sparkbwt.io;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;

import java.io.IOException;

public class ByteRecordReader implements RecordReader<NullWritable, ByteWritable> {

    private FSDataInputStream fileIn;
    private long start;
    private long pos;
    private long end;


    public ByteRecordReader(JobConf job, FileSplit split) throws IOException {

        start = split.getStart();
        end = start + split.getLength();
        final Path file = split.getPath();

        // open the file and seek to the start of the split
        final FileSystem fs = file.getFileSystem(job);
        fileIn = fs.open(file);
        fileIn.seek(start);

        pos = start;
    }

    @Override
    public synchronized boolean next(NullWritable key, ByteWritable value) throws IOException {

        if (pos < end) {
            byte read = fileIn.readByte();
            pos++;

            while (read == '\n' || read == '\r') {
                if (pos < end) {
                    read = fileIn.readByte();
                    pos++;
                } else return false;
            }

            value.set(read);
            return true;
        }

        return false;
    }

    @Override
    public NullWritable createKey() {
        return NullWritable.get();
    }

    @Override
    public ByteWritable createValue() {
        return new ByteWritable();
    }

    @Override
    public synchronized long getPos() {
        return pos;
    }

    @Override
    public synchronized void close() throws IOException {
        if (fileIn != null) {
            fileIn.close();
        }
    }

    @Override
    public synchronized float getProgress() throws IOException {
        if (start == end)
            return 0.0f;
        else
            return Math.min(1.0f, pos - start) / (float) (end - start);

    }

}
