package com.ulfric.commons.nio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.truth.Truth;

import com.ulfric.commons.time.TemporalHelper;
import com.ulfric.veracity.Veracity;
import com.ulfric.veracity.suite.HelperTestSuite;

import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

class FileHelperTest extends HelperTestSuite {

	FileHelperTest() {
		super(FileHelper.class);
	}

	private FileSystem fileSystem;
	private Path file;

	@BeforeEach
	void setup() {
		fileSystem = Jimfs.newFileSystem(Configuration.unix());
		file = fileSystem.getPath("test");
	}

	@Test
	void testDeleteRegularFile() throws Exception {
		Files.createFile(file);
		assertExists();
		FileHelper.delete(file);
		assertNotExists();
	}

	@Test
	void testDeleteNonExistentFile() throws Exception {
		assertNotExists();
		FileHelper.delete(file);
		assertNotExists();
	}

	@Test
	void testDeleteDirectory() throws Exception {
		Files.createDirectory(file);
		assertExists();
		FileHelper.delete(file);
		assertNotExists();
	}

	@Test
	void testDeleteDirectoryTree() throws Exception {
		Files.createDirectory(file);
		Files.createFile(file.resolve("regularfile"));
		Files.createDirectories(file.resolve("subdirectory1").resolve("subdirectory2"));

		assertExists();
		FileHelper.delete(file);
		assertNotExists();
	}

	@Test
	void testDeleteNull() throws Exception {
		Veracity.assertThat(() -> FileHelper.delete(null))
			.doesThrow(NullPointerException.class);
	}

	@Test
	void testCreateFileNull() throws Exception {
		Veracity.assertThat(() -> FileHelper.createFile(null))
			.doesThrow(NullPointerException.class);
	}

	@Test
	void testCreateFile() {
		FileHelper.createFile(file);
		Veracity.assertThat(file).isRegularFile();
	}

	@Test
	void testCreateParentDirectories() {
		Path child = file.resolve("child.txt");
		FileHelper.createParentDirectories(child);
		Veracity.assertThat(child.getParent()).exists();
	}

	@Test
	void testCreateFileAlreadyExists() {
		FileHelper.createFile(file);
		Veracity.assertThat(() -> FileHelper.createFile(file)).runsWithoutExceptions();
	}

	@Test
	void testCreateFileAlreadyDirectory() throws Exception {
		Files.createDirectory(file);
		Veracity.assertThat(() -> FileHelper.createFile(file))
			.doesThrow(UncheckedIOException.class);
	}

	@Test
	void testCreateDefaultFile() {
		Path file = fileSystem.getPath("test.txt");
		FileHelper.createDefaultFile(file);
		Truth.assertThat(FileHelper.read(file)).isEqualTo("test123\n2ndLine");
	}

	@Test
	void testCreateDefaultFileMissing() {
		Path file = fileSystem.getPath("missing.txt");
		FileHelper.createDefaultFile(file);
		Truth.assertThat(FileHelper.read(file)).isEmpty();
	}

	@Test
	void testCreateDefaultFileAlreadyExists() {
		Path file = fileSystem.getPath("test.txt");
		FileHelper.write(file, "already exists");
		FileHelper.createDefaultFile(file);
		Veracity.assertThat(file).contentEquals("already exists");
	}

	@Test
	void testWriteString() {
		Path file = fileSystem.getPath("test.txt");
		FileHelper.write(file, "hello");
		Veracity.assertThat(file).contentEquals("hello");
	}

	@Test
	void testWriteStringBytes() {
		Path file = fileSystem.getPath("test.txt");
		FileHelper.write(file, "hello".getBytes());
		Veracity.assertThat(file).contentEquals("hello");
	}

	@Test
	void testLastModified() {
		FileHelper.write(file, "blah");
		FileHelper.setLastModified(file, TemporalHelper.instantNowMinus(Duration.ofMinutes(5)));
		Instant oldLastModified = FileHelper.getLastModified(file);

		FileHelper.write(file, "anything");
		Instant lastModified = FileHelper.getLastModified(file);

		Truth.assertThat(oldLastModified).isLessThan(lastModified);
	}

	private void assertExists() {
		Veracity.assertThat(file).exists();
	}

	private void assertNotExists() {
		Veracity.assertThat(file).doesNotExist();
	}

}