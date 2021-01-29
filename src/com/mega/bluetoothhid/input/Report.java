package com.mega.bluetoothhid.input;

import com.mega.bluetoothhid.input.ID;

import java.nio.ByteBuffer;
import java.util.Queue;

public interface Report {
	byte[] build();
	Queue<ByteBuffer> getReportQueue();
	Class getHidDescription();
}
