package com.example.myapplication.tool;

import android.os.Message;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class MyRequestBody extends RequestBody {
    public static final int UPDATE = 0x01;
    private RequestBody requestBody;
    private BufferedSink bufferedSink;
    public MyRequestBody(RequestBody body) {
        requestBody = body;

    }


    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        if (bufferedSink==null){
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //刷新
        bufferedSink.flush();
    }

    private Sink sink(BufferedSink sink) {

        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength==0){
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                //回调
                Message msg = Message.obtain();
                msg.what = UPDATE;
               // msg.obj =  new ProgressModel(bytesWritten,contentLength,bytesWritten==contentLength);
               // myHandler.sendMessage(msg);
            }
        };
    }


}
