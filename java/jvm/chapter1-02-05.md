# 本地方法栈
本地方法栈（Native Method Stacks）与虚拟机栈作用相似，也会抛出StackOverflowError和OutOfMemoryError异常。

区别在于虚拟机栈为虚拟机执行Java方法（字节码）服务，而本地方法栈是为虚拟机使用到的Native方法服务。
