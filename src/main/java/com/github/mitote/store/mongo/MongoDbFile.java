package com.github.mitote.store.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.bson.types.ObjectId;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;


public class MongoDbFile implements DataSource {

	private GridFS gridFS;
	private ObjectId id;
	private GridFSDBFile file;
	
	
	public MongoDbFile(GridFS gridFS, InputStream in, String contentType, String fileName) {
		this.gridFS = gridFS;
		GridFSInputFile file = gridFS.createFile( in, fileName );
		file.setContentType( contentType );
		file.save();
		this.id = (ObjectId) file.getId();
	}
	
	
	public MongoDbFile(GridFS gridFS, ObjectId id) {
		this.gridFS = gridFS;
		this.id = id;
	}

	
	public InputStream getInputStream() throws IOException {
		return getFile().getInputStream();
	}

	
	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException("");
	}

	
	public String getContentType() {
		return getFile().getContentType();
	}

	
	public String getName() {
		return getFile().getFilename();
	}

	
	public ObjectId getId() {
		return id;
	}
	
	
	protected GridFSDBFile getFile() {
		if (file == null) {
			file = gridFS.find(id);			
		}
		return file;
	}
	
}
