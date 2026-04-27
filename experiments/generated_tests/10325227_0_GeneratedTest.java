java
package io.soliton.protobuf.plugin;

import static org.mockito.Mockito.*;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileOptions;
import com.google.protobuf.DescriptorProtos.ServiceDescriptorProto;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.List;
import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class ProtoFileHandlerTest {

  @Mock private ProtoServiceHandler serviceHandler;
  @Mock private OutputStream output;
  private List<String> types = new ArrayList<>();

  @Test
  public void handle_singleService() throws IOException {
    FileDescriptorProto.Builder fileProtoBuilder = FileDescriptorProto.newBuilder();
    fileProtoBuilder.setName("test.proto");
    fileProtoBuilder.setPackage("test.package");

    FileOptions.Builder fileOptionsBuilder = FileOptions.newBuilder();
    fileOptionsBuilder.setJavaPackage("test.java.package");
    fileOptionsBuilder.setJavaMultipleFiles(false);
    fileOptionsBuilder.setJavaOuterClassname("TestOuterClass");
    fileProtoBuilder.setOptions(fileOptionsBuilder.build());

    ServiceDescriptorProto.Builder serviceBuilder = ServiceDescriptorProto.newBuilder();
    serviceBuilder.setName("TestService");
    fileProtoBuilder.addService(serviceBuilder.build());

    FileDescriptorProto protoFile = fileProtoBuilder.build();

    ProtoFileHandler handler =
        new ProtoFileHandler() {
          @Override
          String inferJavaPackage(FileDescriptorProto protoFile) {
            return "test.java.package";
          }

          @Override
          String inferOuterClassName(FileDescriptorProto protoFile) {
            return "TestOuterClass";
          }

          @Override
          ProtoServiceHandler createServiceHandler(String javaPackage, List<String> types, boolean multipleFiles, String outerClassName, String protoPackage, OutputStream output) {
            return serviceHandler;
          }

        };

    handler.output = output;
    handler.types = types;

    handler.handle(protoFile);

    verify(serviceHandler, times(1)).handle(any(ServiceDescriptorProto.class));
  }

  @Test
  public void handle_multipleServices() throws IOException {
    FileDescriptorProto.Builder fileProtoBuilder = FileDescriptorProto.newBuilder();
    fileProtoBuilder.setName("test.proto");
    fileProtoBuilder.setPackage("test.package");

    FileOptions.Builder fileOptionsBuilder = FileOptions.newBuilder();
    fileOptionsBuilder.setJavaPackage("test.java.package");
    fileOptionsBuilder.setJavaMultipleFiles(false);
    fileOptionsBuilder.setJavaOuterClassname("TestOuterClass");
    fileProtoBuilder.setOptions(fileOptionsBuilder.build());

    ServiceDescriptorProto.Builder serviceBuilder1 = ServiceDescriptorProto.newBuilder();
    serviceBuilder1.setName("TestService1");
    fileProtoBuilder.addService(serviceBuilder1.build());

    ServiceDescriptorProto.Builder serviceBuilder2 = ServiceDescriptorProto.newBuilder();
    serviceBuilder2.setName("TestService2");
    fileProtoBuilder.addService(serviceBuilder2.build());

    FileDescriptorProto protoFile = fileProtoBuilder.build();

    ProtoFileHandler handler =
        new ProtoFileHandler() {
          @Override
          String inferJavaPackage(FileDescriptorProto protoFile) {
            return "test.java.package";
          }

          @Override
          String inferOuterClassName(FileDescriptorProto protoFile) {
            return "TestOuterClass";
          }

          @Override
          ProtoServiceHandler createServiceHandler(String javaPackage, List<String> types, boolean multipleFiles, String outerClassName, String protoPackage, OutputStream output) {
            return serviceHandler;
          }
        };
    handler.output = output;
    handler.types = types;

    handler.handle(protoFile);

    verify(serviceHandler, times(2)).handle(any(ServiceDescriptorProto.class));
  }

  @Test
  public void handle_multipleFiles() throws IOException {
    FileDescriptorProto.Builder fileProtoBuilder = FileDescriptorProto.newBuilder();
    fileProtoBuilder.setName("test.proto");
    fileProtoBuilder.setPackage("test.package");

    FileOptions.Builder fileOptionsBuilder = FileOptions.newBuilder();
    fileOptionsBuilder.setJavaPackage("test.java.package");
    fileOptionsBuilder.setJavaMultipleFiles(true);
    fileProtoBuilder.setOptions(fileOptionsBuilder.build());

    ServiceDescriptorProto.Builder serviceBuilder = ServiceDescriptorProto.newBuilder();
    serviceBuilder.setName("TestService");
    fileProtoBuilder.addService(serviceBuilder.build());

    FileDescriptorProto protoFile = fileProtoBuilder.build();

    ProtoFileHandler handler =
        new ProtoFileHandler() {
          @Override
          String inferJavaPackage(FileDescriptorProto protoFile) {
            return "test.java.package";
          }

          @Override
          String inferOuterClassName(FileDescriptorProto protoFile) {
            return "TestOuterClass";
          }
          @Override
          ProtoServiceHandler createServiceHandler(String javaPackage, List<String> types, boolean multipleFiles, String outerClassName, String protoPackage, OutputStream output) {
            return serviceHandler;
          }
        };

    handler.output = output;
    handler.types = types;

    handler.handle(protoFile);

    verify(serviceHandler, times(1)).handle(any(ServiceDescriptorProto.class));
  }
  @VisibleForTesting
  ProtoServiceHandler createServiceHandler(String javaPackage, List<String> types, boolean multipleFiles, String outerClassName, String protoPackage, OutputStream output){
    return new ProtoServiceHandler(javaPackage, types, multipleFiles, outerClassName, protoPackage, output);
  }
  @VisibleForTesting
  String inferOuterClassName(FileDescriptorProto protoFile){
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.PASCAL_CASE, Iterables.getLast(Splitter.on('/').split(protoFile.getName())).replace(".proto", ""));
  }
  @VisibleForTesting
  String inferJavaPackage(FileDescriptorProto protoFile){
    Preconditions.checkArgument(protoFile.hasPackage(), "Proto file must have a package name: %s", protoFile.getName());
    return protoFile.getPackage();
  }
  public OutputStream output;
  public List<String> types;
  public void handle(FileDescriptorProto protoFile) throws IOException {
    String javaPackage = inferJavaPackage(protoFile);
    boolean multipleFiles = protoFile.getOptions().getJavaMultipleFiles();
    String outerClassName = null;
    if (!multipleFiles) {
      if (protoFile.getOptions().hasJavaOuterClassname()) {
        outerClassName = protoFile.getOptions().getJavaOuterClassname();
      } else {
        outerClassName = inferOuterClassName(protoFile);
      }
    }
    ProtoServiceHandler serviceHandler = createServiceHandler(javaPackage, types,
        multipleFiles, outerClassName, protoFile.getPackage(), output);
    for (ServiceDescriptorProto service : protoFile.getServiceList()) {
      serviceHandler.handle(service);
    }
  }
}