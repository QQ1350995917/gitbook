package pwd.java.bytecode.asm;

import java.io.File;
import java.io.FileOutputStream;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;

/**
 * pwd.java.bytecode@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-01-06 15:11
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class OriginGenerator {

  public static void main(String[] args) throws Exception {
    //读取
    ClassReader classReader = new ClassReader("pwd/java/bytecode/asm/Origin");
    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    //处理
    OriginClassVisitor classVisitor = new OriginClassVisitor(classWriter);
    classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
    byte[] data = classWriter.toByteArray();
    //输出
    File f = new File("zythum/java-bytecode/target/classes/pwd/java/bytecode/asm/Origin.class");
    FileOutputStream fout = new FileOutputStream(f);
    fout.write(data);
    fout.close();
    System.out.println("now generator cc success!!!!!");
  }
}
