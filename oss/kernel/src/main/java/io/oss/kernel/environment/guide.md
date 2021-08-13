#定义各个模块的 运行环境示例

##1.制定环境实体
如KernelEnvironment

`  public KernelEnvironment() {
         //定义namespace
         super(KernelEnvironmentName);
     }
`

构造器中定义命名空间

##2.定义环境加载器

  
    public class KernelEnvironmentLoader extends AbstractEnvironmentLoader{

   
       public KernelEnvironmentLoader() {
           super("kernel.properties");
   
       }
   
       @Override
       protected NamedEnvironment buildEnvironment(Properties properties) {
           KernelEnvironment kernelEnvironment = new KernelEnvironment();
           kernelEnvironment.putAll(properties);
           if (PlatformUtil.isLinux()) {
               kernelEnvironment.addProperty(KernelEnvironment.PLATFORM_TYPE, "linux");
           } else {
               kernelEnvironment.addProperty(KernelEnvironment.PLATFORM_TYPE, "other");
           }
           return kernelEnvironment;
       }
    }

构造器指定配置文件路径