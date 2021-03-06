package com.ulfric.commons.nio;
import com.ulfric.tryto.TryTo;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Objects;

public class FileHelper {

	public static void delete(Path file) {
		Objects.requireNonNull(file, "file");

		if (Files.notExists(file)) {
			return;
		}

		TryTo.runIo(() -> {
			if (Files.isRegularFile(file)) {
				Files.delete(file);
				return;
			}

			Files.walkFileTree(file, new DeletionFileVisitor());
		});
	}

	public static void createDefaultFile(Path file) {
		if (Files.exists(file)) {
			return;
		}
		createFile(file);

		String resource = ResourceHelper.getResourceText(file.toString());

		if (resource == null) {
			return;
		}

		write(file, resource);
	}

	public static void createFile(Path file) {
		Objects.requireNonNull(file, "file");

		if (Files.isRegularFile(file)) {
			return;
		}

		createParentDirectories(file);
		TryTo.runIo(() -> Files.createFile(file));
	}

	public static void createParentDirectories(Path file) {
		Objects.requireNonNull(file, "file");

		if (Files.exists(file)) {
			return;
		}

		Path parent = file.getParent();
		if (parent == null) {
			return;
		}

		createDirectories(parent);
	}

	public static void createDirectories(Path file) {
		Objects.requireNonNull(file, "file");

		TryTo.runIo(() -> Files.createDirectories(file));
	}

	public static void write(Path file, String value) {
		Objects.requireNonNull(value, "value");

		write(file, value.getBytes());
	}

	public static void write(Path file, byte[] value) {
		Objects.requireNonNull(file, "file");
		Objects.requireNonNull(value, "value");

		TryTo.runIo(() -> Files.write(file, value));
	}

	public static String read(Path file) {
		Objects.requireNonNull(file, "file");

		return new String(TryTo.getIo(() -> Files.readAllBytes(file)));
	}

	public static BufferedReader newBufferedReader(Path file) {
		Objects.requireNonNull(file, "file");

		return TryTo.getIo(() -> Files.newBufferedReader(file));
	}

	public static Instant getLastModified(Path file) {
		return TryTo.getIo(() -> Files.getLastModifiedTime(file)).toInstant();
	}

	public static void setLastModified(Path file, Instant when) {
		TryTo.runIo(() -> Files.setLastModifiedTime(file, FileTime.from(when)));
	}

	private FileHelper() {
	}

}