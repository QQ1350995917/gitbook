# 块存储-对象存储-文件存储

这三者的本质差别是使用数据的“用户”不同：块存储的用户是可以读写块设备的软件系统，例如传统的文件系统、数据库；文件存储的用户是自然人；对象存储的用户则是其它计算机软件。

首先要说明一下的是，这三个概念都是分布式存储中的概念，由不同的网络存储协议实现。不过“网络”和“存储”的结合本身会对解释这三个概念的本质差异带来不便，下面的解释中我会先解释存储本身，之后再结合网络来说明。

## 文件存储

文件存储的用户是自然人，最容易理解。计算机中所有的数据都是0和1，存储在硬件介质上的一连串的01组合对我们来说完全无法去分辨以及管理。因此我们用“文件”这个概念对这些数据进行组织，所有用于同一用途的数据，按照不同应用程序要求的结构方式组成不同类型的文件（通常用不同的后缀来指代不同的类型），然后我们给每一个文件起一个方便理解记忆的名字。而当文件很多的时候，我们按照某种划分方式给这些文件分组，每一组文件放在同一个目录（或者叫文件夹）里面，当然我们也需要给这些目录起一个容易理解和记忆的名字。而且目录下面除了文件还可以有下一级目录（称之为子目录或者子文件夹），所有的文件、目录形成一个树状结构。我们最常用的Windows系统中，打开资源管理器就可以看到以这种方式组织起来的无数个文件和目录。在Linux可以用tree命令列出以某个文件夹为根节点列出一棵树：

```
[root@mysqldb conf]# tree ./
./
├── fastcgi.conf
├── fastcgi.conf.default
├── fastcgi_params
├── fastcgi_params.default
├── koi-utf
├── koi-win
├── mime.types
├── mime.types.default
├── nginx.conf
├── nginx.conf.default
├── scgi_params
├── scgi_params.default
├── uwsgi_params
├── uwsgi_params.default
├── vhost
│   ├── 5656-user.conf
│   └── reload.sh
└── win-utf

1 directory, 24 files
```

为了方便查找，从根节点开始逐级目录往下，一直到文件本身，把这些目录、子目录、文件的名字用特殊的字符（例如Windows/DOS用“\”，类Unix系统用“/”）拼接起来，这样的一串字符称之为路径，例如Linux中的“/etc/systemd/system.conf”或者Windows中的“C:\Windows\System32\taskmgr.exe”。人类用路径作为唯一标识来访问具体的文件。而由作为自然人的程序员所编写的各种软件程序，绝大部分也使用这种方式来访问文件。

把存储介质上的数据组织成目录-子目录-文件这种形式的数据结构，用于从这个结构中寻找、添加、修改、删除文件的程序，以及用于维护这个结构的程序，组成的系统有一个专用的名字：文件系统（File System）。文件系统有很多，常见的有Windows的FAT/FAT32/NTFS，Linux的EXT2/EXT3/EXT4/XFS/BtrFS等。而在网络存储中，底层数据并非存储在本地的存储介质，而是另外一台服务器上，不同的客户端都可以用类似文件系统的方式访问这台服务器上的文件，这样的系统叫网络文件系统（Network File System），常见的网络文件系统有Windows网络的CIFS（也叫SMB）、类Unix系统网络的NFS等。而文件存储除了网络文件系统外，FTP、HTTP其实也算是文件存储的某种特殊实现，都是可以通过某个url来访问一个文件。

### 典型设备：FTP、NFS服务器

为了克服上述文件无法共享的问题，所以有了文件存储。

文件存储也有软硬一体化的设备，但是其实普通拿一台服务器/笔记本，只要装上合适的操作系统与软件，就可以架设FTP与NFS服务了，架上该类服务之后的服务器，就是文件存储的一种了。

主机A可以直接对文件存储进行文件的上传下载，与块存储不同，主机A是不需要再对文件存储进行格式化的，因为文件管理功能已经由文件存储自己搞定了。

#### 优点：

* 造价交低：随便一台机器就可以了，另外普通以太网就可以，根本不需要专用的SAN网络，所以造价低。
*   方便文件共享：例如主机A（WIN7，NTFS文件系统），主机B（Linux，EXT4文件系统），想互拷一部电影，本来不行。加了个主机C（NFS服务器），然后可以先A拷到C，再C拷到B就OK了。（例子比较肤浅，请见谅……）

    **缺点：**
* 读写速率低，传输速率慢，以太网，上传下载速度较慢，
* 另外所有读写都要1台服务器里面的硬盘来承担，相比起磁盘阵列动不动就几十上百块硬盘同时读写，速率慢了许多。

## 对象存储

对象存储其实介于块存储和文件存储之间。文件存储的树状结构以及路径访问方式虽然方便人类理解、记忆和访问，但计算机需要把路径进行分解，然后逐级向下查找，最后才能查找到需要的文件，对于应用程序来说既没必要，也很浪费性能。

而块存储是排它的，服务器上的某个逻辑块被一台客户端挂载后，其它客户端就无法访问上面的数据了。而且挂载了块存储的客户端上的一个程序要访问里面的数据，不算类似数据库直接访问裸设备这种方式外，通常也需要对其进行分区、安装文件系统后才能使用。除了在网络上传输的数据包效率更高以外，并不比使用文件存储好多少，客户端的文件系统依然需要对路径分解，然后逐级查找才能定位到某一个具体的文件。

是否可以用不排它但又类似块设备访问的方式呢？理论上是可以的，但对块设备的访问方式虽然比文件存储快，其实也很麻烦——一个文件往往是由多个块组成，并且很可能是不连续的。例如要读取一个文件，可能需要发出这样的指令：

* 读取从编号A₁开始的N₁个块；
* 读取从编号A₂开始的N₂个块；
* 读取从编号A₃开始的N₃个块；
* …………
* 读取从编号Ai开始的Ni个块。

最后自行把这i个连续的块自行拼接成一个文件，这才完成了一个文件的读取操作。为了发出这些指令，访问文件的软件系统需要记录下这个文件分成多少个部分，每个部分的起始块编号是多少，有多少块，顺序如何。不单是读取操作，删除、写入、修改操作也是如此，非常麻烦复杂。而且往往一个文件可能需要被多个系统访问使用，这就更麻烦了。

为了解决这中麻烦，使用一个统一的底层存储系统，管理这些文件和底层介质的组织结构，然后给每个文件一个唯一的标识，其它系统需要访问某个文件，直接提供文件的标识就可以了。存储系统可以用更高效的数据组织方式来管理这些标识以及其对应的存储介质上的块。

当然，对于不同的软件系统来说，一次访问需要获取的不一定是单个我们传统意义上的文件，根据不同的需要可能只是一个/组值，某个文件的一部分，也可能是多个文件的组合，甚至是某个块设备，统称为对象。这就是对象存储。

### 典型设备：内置大容量硬盘的分布式服务器

对象存储最常用的方案，就是多台服务器内置大容量硬盘，再装上对象存储软件，然后再额外搞几台服务作为管理节点，安装上对象存储管理软件。管理节点可以管理其他服务器对外提供读写访问功能。

之所以出现了对象存储这种东西，是为了克服块存储与文件存储各自的缺点，发扬它俩各自的优点。简单来说块存储读写快，不利于共享，文件存储读写慢，利于共享。能否弄一个读写快，利 于共享的出来呢。于是就有了对象存储。

首先，一个文件包含了了属性（术语叫metadata，元数据，例如该文件的大小、修改时间、存储路径等）以及内容（以下简称数据）。

以往像FAT32这种文件系统，是直接将一份文件的数据与metadata一起存储的，存储过程先将文件按照文件系统的最小块大小来打散（如4M的文件，假设文件系统要求一个块4K，那么就将文件打散成为1000个小块），再写进硬盘里面，过程中没有区分数据/metadata的。而每个块最后会告知你下一个要读取的块的地址，然后一直这样顺序地按图索骥，最后完成整份文件的所有块的读取。

这种情况下读写速率很慢，因为就算你有100个机械手臂在读写，但是由于你只有读取到第一个块，才能知道下一个块在哪里，其实相当于只能有1个机械手臂在实际工作。而对象存储则将元数据独立了出来，控制节点叫元数据服务器（服务器+对象存储管理软件），里面主要负责存储对象的属性（主要是对象的数据被打散存放到了那几台分布式服务器中的信息），而其他负责存储数据的分布式服务器叫做OSD，主要负责存储文件的数据部分。当用户访问对象，会先访问元数据服务器，元数据服务器只负责反馈对象存储在哪些OSD，假设反馈文件A存储在B、C、D三台OSD，那么用户就会再次直接访问3台OSD服务器去读取数据。

这时候由于是3台OSD同时对外传输数据，所以传输的速度就加快了。当OSD服务器数量越多，这种读写速度的提升就越大，通过此种方式，实现了读写快的目的。

另一方面，对象存储软件是有专门的文件系统的，所以OSD对外又相当于文件服务器，那么就不存在文件共享方面的困难了，也解决了文件共享方面的问题。所以对象存储的出现，很好地结合了块存储与文件存储的优点。最后为什么对象存储兼具块存储与文件存储的好处，还要使用块存储或文件存储呢？

* 有一类应用是需要存储直接裸盘映射的，例如数据库。因为数据库需要存储裸盘映射给自己后，再根据自己的数据库文件系统来对裸盘进行格式化的，所以是不能够采用其他已经被格式化为某种文件系统的存储的。此类应用更适合使用块存储。
* 对象存储的成本比起普通的文件存储还是较高，需要购买专门的对象存储软件以及大容量硬盘。如果对数据量要求不是海量，只是为了做文件共享的时候，直接用文件存储的形式好了，性价比高。

## 块存储

传统的文件系统，是直接访问存储数据的硬件介质的。介质不关心也无法去关心这些数据的组织方式以及结构，因此用的是最简单粗暴的组织方式：所有数据按照固定的大小分块，每一块赋予一个用于寻址的编号。以大家比较熟悉的机械硬盘为例，一块就是一个扇区，老式硬盘是512字节大小，新硬盘是4K字节大小。老式硬盘用柱面-磁头-扇区号（CHS，Cylinder-Head-Sector）组成的编号进行寻址，现代硬盘用一个逻辑块编号寻址（LBA，Logical Block Addressing）。所以，硬盘往往又叫块设备（Block Device）,当然，除了硬盘还有其它块设备，例如不同规格的软盘，各种规格的光盘，磁带等。

至于哪些块组成一个文件，哪些块记录的是目录/子目录信息，这是文件系统的事情。不同的文件系统有不同的组织结构，这个就不展开了。为了方便管理，硬盘这样的块设备通常可以划分为多个逻辑块设备，也就是我们熟悉的硬盘分区（Partition）。反过来，单个介质的容量、性能有限，可以通过某些技术手段把多个物理块设备组合成一个逻辑块设备，例如各种级别的RAID，JBOD，某些操作系统的卷管理系统（Volume Manager）如Windows的动态磁盘、Linux的LVM等。

补充一下的是，块设备的使用对象除了传统的文件系统以及一些专用的管理工具软件如备份软件、分区软件外，还有一些支持直接读写块设备的软件如数据库等，但一般用户很少这样使用。

在网络存储中，服务器把本地的一个逻辑块设备——底层可能是一个物理块设备的一部分，也可能是多个物理块设备的组合，又或者多个物理块设备的组合中的一部分，甚至是一个本地文件系统上的一个文件——通过某种协议模拟成一个块设备，远程的客户端（可以是一台物理主机，也可以是虚拟机，某个回答所说的块设备是给虚拟机用是错误的）使用相同的协议把这个逻辑块设备作为一个本地存储介质来使用，划分分区，格式化自己的文件系统等等。这就是块存储，比较常见的块存储协议是iSCSI。

### 典型设备：磁盘阵列，硬盘

块存储主要是将裸磁盘空间整个映射给主机使用的，就是说例如磁盘阵列里面有5块硬盘（为方便说明，假设每个硬盘1G），然后可以通过划逻辑盘、做Raid、或者LVM（逻辑卷）等种种方式逻辑划分出N个逻辑的硬盘。（假设划分完的逻辑盘也是5个，每个也是1G，但是这5个1G的逻辑盘已经于原来的5个物理硬盘意义完全不同了。例如第一个逻辑硬盘A里面，可能第一个200M是来自物理硬盘1，第二个200M是来自物理硬盘2，所以逻辑硬盘A是由多个物理硬盘逻辑虚构出来的硬盘。）

接着块存储会采用映射的方式将这几个逻辑盘映射给主机，主机上面的操作系统会识别到有5块硬盘，但是操作系统是区分不出到底是逻辑还是物理的，它一概就认为只是5块裸的物理硬盘而已，跟直接拿一块物理硬盘挂载到操作系统没有区别的，至少操作系统感知上没有区别。

此种方式下，操作系统还需要对挂载的裸硬盘进行分区、格式化后，才能使用，与平常主机内置硬盘的方式完全无异。

#### 优点

* 这种方式的好处当然是因为通过了Raid与LVM等手段，对数据提供了保护。
* 另外也可以将多块廉价的硬盘组合起来，成为一个大容量的逻辑盘对外提供服务，提高了容量。
* 写入数据的时候，由于是多块磁盘组合出来的逻辑盘，所以几块磁盘可以并行写入的，提升了读写效率。
*   很多时候块存储采用SAN架构组网，传输速率以及封装协议的原因，使得传输速度与读写速率得到提升。

    **缺点**
* 采用SAN架构组网时，需要额外为主机购买光纤通道卡，还要买光纤交换机，造价成本高。
* 主机之间的数据无法共享，在服务器不做集群的情况下，块存储裸盘映射给主机，再格式化使用后，对于主机来说相当于本地盘，那么主机A的本地盘根本不能给主机B去使用，无法共享数据。
* 不利于不同操作系统主机间的数据共享：另外一个原因是因为操作系统使用不同的文件系统，格式化完之后，不同文件系统间的数据是共享不了的。例如一台装了WIN7/XP，文件系统是FAT32/NTFS，而Linux是EXT4，EXT4是无法识别NTFS的文件系统的。就像一只NTFS格式的U盘，插进Linux的笔记本，根本无法识别出来。所以不利于文件共享。

## 分布式存储

所谓分布式存储，就是这个底层的存储系统，因为要存放的数据非常多，单一服务器所能连接的物理介质是有限的，提供的IO性能也是有限的，所以通过多台服务器协同工作，每台服务器连接若干物理介质，一起为多个系统提供存储服务。为了满足不同的访问需求，往往一个分布式存储系统，可以同时提供文件存储、块存储和对象存储这三种形式的服务。

## 参考资料

[https://www.zhihu.com/question/21536660/answer/1159036357](https://www.zhihu.com/question/21536660/answer/1159036357) [https://www.zhihu.com/question/21536660/answer/33279921](https://www.zhihu.com/question/21536660/answer/33279921)
