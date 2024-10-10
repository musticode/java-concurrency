# Concurrency

### Thread States

- Java'da 6 thread state'i vardır. İşletim sisteminin ise sadece 5 thread state'i bulunmaktadır. Ancak, JDK 1.2'den sonra Java'daki iş parçacıklarının özünün aslında işletim sistemindeki iş parçacıkları olduğu unutulmamalıdır. Farklı işletim sistemleri için, farklı tasarım fikirleri nedeniyle, iş parçacıklarının tasarımında da çeşitli farklılıklar vardır, bu nedenle JVM tasarımında zaten şunları beyan etmiştir; java sanal makinesindeki thread durumu, herhangi bir işletim sistemi thread durumunu yansıtmaz. Bundan dolayı java thread state'leri sadece bir anlayış modeli olarak kabul edilebilir. Java thread'leri ve işletim sistemi thread'leri aslında aynı köktür ancak farklılardır.

### `wait();` & `notify();` metotlarını kullanırken mutlaka dikkat edilmesi gereken önlemler

- wait ve notify metotlarını kullanırken `synchronized` bloğu içinde yazmamız gerekir. Onun haricinde yazarsak exception fırlatılacaktır.

```java
Queue<String> buffer = new LinkedList<String>();
public synchronized void save(String data){
    System.out.println("Produce a data");
    buffer.add(data);
    notify();
}

public synchronized String take() throws InterruptedException {
    System.out.println("Try to consume a data");
    while (buffer.isEmpty()){
        wait();
    }
    return buffer.remove();
}
```

Standart bir producer & consumer örneği

- `wait` metodu neden loop operasyonları içinde kullanılmalıdır?

1. Bir thread wait metodunu çağırdıktan sonra, sahte bir notify olasılığı vardır. Yani thread notify/notifyAll tarafından çağırılmadan kesintiye uğramadan veya zaman aşımı olmadan uyandırılabilir. Bu da olmasını istemediğimiz bir şeydir
2. Yanlış uyandırma ihtimali gerçek dünya örneklerinde çok küçük olsa da proğramın yanlış uyandırılma durumlarında da doğruluğu garanti altına alınmalıdır. Bundan dolayı da while döngüsü ile kullanılması gerekir.

```
while(condition does not hold)
    obj.wait();
```

Bu şekilde, eğer yanlışlıkla da uyandırılırsa condition'lar while ile sürekli kontrol edilecektir. Condition'lar uymazsa wait devam edecek, bu da yanlış uyandırılma risklerini elimine edecektir.

- wait/notify ve sleep metotları arasındaki benzerlikler ve farklılıklar nelerdir?

wait ve sleep metotlarının benzerlikleri :

1. İkisi de thread'leri bloklayabilir
2. Eğer bu metotlar bekleme sürecinde bir interrupt sinyali alırsa bu duruma InterruptedException exception'uyla cevap verebilir.

wait ve sleep metotlarının farkları :

1. `wait` metodu sycnronized ile beraber kullanılmalıdır, `sleep` metodu böyle bir gereksinime ihtiyaç duymaz
2. `sleep` metodu sycnronized kodu içinde çalıştırıldığında monitor kilidinir bırakmaz, ama `wait` metodu aktif şekilde monitor kilidini bırakır.
3. `sleep` metodu tanımlanmak için bir süre bilgisi gerektirir ve thread işine bu süre bittiğinde aktif şekilde devam eder. Parametresiz geçilen `wait` metodu kesintiye uğramadan veya uyandırılana kadar kalıcı bekleme durumuna gekir. Aktif olarak bu thread devam etmez.
4. wait/notify metotları object class'ının metotlarıdır. sleep metodu ise Thread class'ının metodudur.

## Thread group nedir? Fonksiyonları nelerdir?

#### Thread group intro

```java
public static void main(String[] args) {

    Thread subThread = new Thread(()-> {
        System.out.println("SUB");
        System.out.println("The name of the thread group is : " +
                Thread.currentThread().getThreadGroup().getName()
        );

        System.out.println("Thread name : " +
                Thread.currentThread().getName()
        );
    });

    subThread.start();
    System.out.println("MAIN");
    System.out.println("The name of the thread group is : " +
            Thread.currentThread().getThreadGroup().getName()
    );

    System.out.println("Thread name : " +
            Thread.currentThread().getName()
    );
}
```

- Java'da ThreadGroup, bir thread grubunu temsil etmek için kullanılır. ThreadGroup'u, thread'leri gruplar halinde kontrol etmek ve thread'leri daha rahat yönetmek için kullanabiliriz.
- ThreadGroup ve Thread arasındaki ilişki isimleri gibidir. Bir thread grup içinde bulur ve thread, gruptan bağımsız bulunmaz.
- main() metodunu çalıştıran thread'in adı main'dir ve new Thread çalıştırıldığında açıkça bir ThreadGroup belirtmezsek varsayılan olarak ana thread'in ThreadGroup'unu kendi ThreadGroup'umuza ayarlarız.

Thread grupları parent-child şeklinde dizayn edilmişlerdir. Bir thread grup başka thread gruplarına entegre olabilir ve aynı zamanda başka child thread gruplarına entegre olabilir. Yapısal olarak bir thread grup ağaç(tree) yapısındadır. Ağacın en üstünde sistem thread'i bulunur. Onun altında da main thread ve main'in içinde de thread grupları bulunur. Bu thread grupları da kendi thread'lerini barındırır.

- JVM tarafından oluşturulan sistem thread'i, nesnelerin imhası(Garbage Collecor), JVM'in sistem görevlerini işlemek için kullanılan bir thread grubudur.
- Sistem thread grubunun altında bulunan main thread grubu da genel işlemi içeren thread grubudur.
- Ana thread grubunun alt thread grupları, uygulama tarafından oluşturulan thread gruplarıdır.

#### Thread group construction

```java
// When the JVM starts, it is called to create the root thread group.
private ThreadGroup() { 
    this.name = "system";
    this.maxPriority = Thread.MAX_PRIORITY;
    this.parent = null;
}
//By default, the current ThreadGroup is passed in as the parent ThreadGroup. The parent thread group of the new thread group is the thread group of the currently running thread.
public ThreadGroup(String name) {
    this(Thread.currentThread().getThreadGroup(), name);
}
// Pass in the name to create a thread group, and the parent thread is specified by the client.
public ThreadGroup(ThreadGroup parent, String name) {
    this(checkParentAccess(parent), parent, name);
}
// Main private constructor, most parameters are inherited from the parent thread group
private ThreadGroup(Void unused, ThreadGroup parent, String name) {
    this.name = name;
    this.maxPriority = parent.maxPriority;
    this.daemon = parent.daemon;
    this.vmAllowSuspension = parent.vmAllowSuspension;
    this.parent = parent;
    parent.add(this);
}
```

#### Introduction to the methods included in ThreadGroup

```java
public static void main(String[] args) throws InterruptedException {
    ThreadGroup threadGroup1 = new ThreadGroup("sub1");
    Thread t1 = new Thread(threadGroup1, "t1 in sub1");
    Thread t2 = new Thread(threadGroup1, "t2 in sub1");
    Thread t3 = new Thread(threadGroup1, "t3 in sub1");

    t1.start();
    Thread.sleep(50);
    t2.start();

    int activeThreadCount = threadGroup1.activeCount();
    System.out.println("Active thread in " + threadGroup1.getName() + " thread group : " + activeThreadCount);

    ThreadGroup threadGroup2 = new ThreadGroup("sub2");
    Thread t4 = new Thread(threadGroup2, "t4 in sub2");

    ThreadGroup currentThreadGroup = Thread.currentThread().getThreadGroup();
    int activeGroupCount = currentThreadGroup.activeGroupCount();
    System.out.println("Active thread groups in " + currentThreadGroup.getName() + " thread group: " + activeGroupCount);

    System.out.println("Prints info about currentThreadGroup to the standard group");
    currentThreadGroup.list();
}
```

#### Özet

- Özetle bir thread group tree şeklinde bir yapıya sahiptir. Her thread grubunun altında birden fazla thread veya thread grubu bulunabilir. Thread grupları thread'lerin önceliğini tekdüze bir şekilde kontrol etmek ve thread'lerin izinlerin kontrol etmek vs gibi işlemler için kullanılabilir.

## Thread önceliği nedir? Neden kullanılması önerilmez?

- Bir thread önceliği, işletim sisteminin thread'lerini planlarken her thread'e atadığı yürütme sırasındaki öncelkitir. Daha yüksek önceliğe sahip olan thread'ler daha düşük olanlardan önce yürütülür.
- Java'da 1'den 10'a kadar bir öncelik aralığı vardır. Default ise 5'tir
- Tüm işletim sistemleri 10 level'lik bir öncelik sistemini desteklemiyor, bazıları 3 levellik destek veriyor: low, medium, high
- Java, işletim sistemine yalnızca thread önceliği için bir referans değeri sağlar. İşletim sistemindeki thread'lerin son önceliği hala kendisi tarafından belirlenir.
- Thread'lerin yürütülme sırası nihayetinde işletim sistemindeki scheduler tarafından  belirlenir ve thread'in önceliği thread çağırılmadan önce kesinlikle ayarlanır.
- Genellikle daha yüksek önceliğe sahip thread'lerin yürütülme şansı daha düşük önceliğe sahip iş parçacıklarına göre daha yüksek olur. Bir thread'in önceliğini ayarlamak iiçn Thread sınıfının setPriority() metodunu kullanırız.

##### Neden öncelik kulllanmak önerilmez?

- Java'daki öncelikler güvenilir değildir. Bir java programındaki thread'e ayaralanan öncelik, işletim sistemine yalnızca bir öneridir ve işletim sistemi bunu mutlaka uygulamaz. Gerçek yürütme sırası işletim sisteminin thread zamanlama algoritması tarafından belirlenir.

```java
Thread t1 = new Thread(new MyRunnable());
t1.setPriority(1);

Thread t2 = new Thread(new MyRunnable());
t2.setPriority(5);

Thread t3 = new Thread(new MyRunnable());
t3.setPriority(10);

t3.start();
t2.start();
t1.start();

}

static class MyRunnable implements Runnable{
@Override
public void run() {
    System.out.printf("The currently executing thread is %s, priority : %d%n",
            Thread.currentThread().getName(),
            Thread.currentThread().getPriority()
    );
}
}
```

Burdaki çıktılar her seferinde farklı oluyor, bizim verdiğimiz önceliklere göre de çalışmıyor.

```
The currently executing thread is Thread-1, priority : 5
The currently executing thread is Thread-2, priority : 10
The currently executing thread is Thread-0, priority : 1
```

## Bir thread'i doğru şekilde nasıl durdururuz?

#### Bir thread'i durdurmak ne zaman gereklidir?

- Bir thread çalıştığında doğal olarak en sona kadar çalışmaya devam eder. Ama bazı durumlarda da thread'lerin durdurulması gerekiyor, bu durumlar ise

> - Çalışmanın kullanıcı tarafından iptal edilmesi
> - Runtime ve timeout hataları oluştuğunda
> - Servis kapandığında

#### Bir thread'i sorunsuz şekilde nasıl durdurabiliriz?

- Bir thread'e yürütmeyi kesmesi gerektiğini bildirmek için `interrupt` metodunu kullanabiliriz ve kesilen thread karar verme gücüne sahiptir, yani yalnızca bu kesmeye ne zaman yanıt vereceğine ve ne zaman duracağına karar vermekle kalmaz, aynı zamanda onu yok sayabilir de.
- Başka bir deyişle durdurulan bir thread kesintiye uğramak istemiyorsa onun tamamlanmasını beklemek veya işlemi kapanmaya zorlamak dışında yapabileceğimiz çok bir şey yoktur.

#### Neden durdurmaya zorlamıyoruz? bunun yerine notify ediyoruz

- Aslında bir thread'i durdurmak istediğimiz çoğu zamanda thread'in çalışmayı bitirmesine kadar genelde izin veriyoruz. Örnek olarak bilgisayarı kapatsak bile diğer uygulamaların kapanmasına ve toparlanmasına müsade ediyoruz, bazı satete'leri tutmasını kaydetmesini engelleyemiyoruz.
- Aynısı thread'ler için de geçerlidir. Kesmek istediğimiz thread bizim tarafımızdan geliştirilmemiş olabileceğinden, bu thread tarafından yürütülen iş mantığına hiç aşina değilizdir. Durmasını istiyorsak, hemen durup kaotik bir durumda bırakmak yerine, durmadan önce bir dizi kaydetme ve devretme işini tamamlamasını umarız.

#### Örnek

##### Hatalı durdurma

```java
public class StopThreadDemo implements Runnable{
    @Override
    public void run() {
        System.out.println("Start moving");
        for (int i = 1; i <= 5; i++){
            int j = 50000;
            while (j > 0){
                j--;
            }
            System.out.println(i + " batches have been moved");
        }
        System.out.println("End of moving..!");

    }

    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(new StopThreadDemo());
        a.start();
        Thread.sleep(2);
        a.stop();
    }
}
```

###### `interrupt` metodunu direkt olarak kullanmak

```java
public class StopThreadDemo implements Runnable{
    @Override
    public void run() {
        System.out.println("Start moving");
        for (int i = 1; i <= 5; i++){
            int j = 50000;
            while (j > 0){
                j--;
            }
            System.out.println(i + " batches have been moved");
        }
        System.out.println("End of moving..!");

    }

    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(new StopThreadDemo());
        a.start();
        Thread.sleep(2);
        a.interrupt();
    }
}
```

output :

```
Start moving
1 batches have been moved
2 batches have been moved
3 batches have been moved
4 batches have been moved
5 batches have been moved
End of moving..!
```

> Daha önce de belirttiğimiz gibi, bu thread'in durup durmaması kendisine bağlıdır, dolayısıyla bu thread'in mantığını interrupt'ın interrupt'ına yanıt verecek şekilde değiştirmemiz gerekir, böylece thread durdurulabilir.

###### `interrupt` metodunu kullanırken flag ile kontrol etmek

```java
public class StopThreadDemo implements Runnable{

    @Override
    public void run() {
        System.out.println("Start moving");
        for (int i = 1; i <= 5; i++){
        
            if (Thread.currentThread().isInterrupted()){
                // do sth finishing works
                break;
            }
        
        
            int j = 50000;
            while (j > 0){
                j--;
            }
            System.out.println(i + " batches have been moved");
        }
        System.out.println("End of moving..!");

    }

    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(new StopThreadDemo());
        a.start();
        Thread.sleep(2);
        a.interrupt();
    }
}
```

##### Bir thread'in interrup edilmesi anında thread uyku halinde

- Yani programımız sleep metodunun mantığına sahip olduğunda veya wait, join vb. gibi iş parçacıklarını engelleyebilen ve kesintiye uğrayabilen metotlara sahip olduğunda, InterruptedException istisnasını ele almaya dikkat etmemiz gerekir. Bunu catch'e koyabiliriz, böylece iş parçacığı engelleme sürecine girdiğinde, kesintiye yanıt verebilir ve onu işleyebilir.
- sleep() kesmeye yanıt verdiğinde isInterrupted() metodundaki bayrağı sıfırlayacak olmasıdır, dolayısıyla yukarıdaki koddaki döngü koşulu kontrolünde, Thread.currentThread().isInterrupted()'ın sonucu her zaman false olur ve bu da programın çıkış yapamamasına neden olur.

```java
    @Override
public void run() {
    System.out.println("Start moving...");
    for (int i = 1; i <= 5; i++) {
        if(Thread.currentThread().isInterrupted()) {
            //Do some finishing work.
            break;
        }
        //Simulation of time required to move
        try {
            Thread.sleep(1);
            System.out.println(i + " batches have been moved");
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    System.out.println("End of moving");
}
```

##### re-interrupt

- Daha önce belirtildiği gibi, bir interrupt gerçekleştiğinde ve bir InterruptedException fırlatıldığında, isInterrupted'ın sonucu false olarak sıfırlanır. Ancak, tekrar interrupt'ı çağırmak için destek vardır, bu da isInterrupted'ın sonucunu true yapar.

```java
@Override
public void run() {
    try {
        move();
    } catch (InterruptedException e) {
        System.out.println(e.getMessage());
        Thread.currentThread().interrupt();
    }
    if (Thread.currentThread().isInterrupted()) {
        goBack();
    }
}
```

##### bir interrupt meydana gelip gelmediğini belirleme yöntemleri

- boolean isInterrupted() -> Thread'in kesilip kesilmediğini belirler
- static boolean iterrupted() -> current thread interrupt edildiyse onu belirler, ama interrupted thred flag'ini false'a çeker

#### JDK interrupt'lara yanıt verebilecek yerleşik yöntemlerin bir listesine sahiptir

```
Object.wait() / wait(long) / wait(long, int)
Thread.sleep(long) / sleep(long, int)
Thread.join() / join( long) / join(long, int)
java.util.concurrent.BlockingQueue.take() / put (E)
java.util.concurrent.locks.Lock.lockInterruptibly()
java.util.concurrent.CountDownLatch.await
java.util.concurrent.CyclicBarrier.await
java.util.concurrent.Exchanger.exchange(v)
Related methods of java.nio.channels.InterruptibleChannel
Related methods of java.nio.channels.Selector
```

## Shutdown Hook'u kapsamlı şekilde anlamak

- Program exit'e gelmeden önce veritabanı, bağlantılar, kaynakları vs kapatmak gibi işler olabilir. Bu durumda uygulamaya önceden bir veya daha fazla hook thread'i enjekte edilebilir. Böylece program exit'e gelmeden önce Hook thread'i başlatılır ve yürütülür.
- Örnek senaryolar:
  - Uygulamadaki resource'ları serbest bırakmak
  - Servisi kapatmak(db connection)
  - Notification göndermek, mail, text mesajı gibi
  - Log'ları kaydetmek
- Hook 'thread'leri yalnızca çıkış sinyali doğru alındığında doğru şekilde yürütülebilir. İşlemi kill -9 gibi zorunlu bir yöntemle sonlandırmaya zorlarsanız, işlem Hook thread'ini yürütmez. Esasen, yapabilecekleri hiçbir şey yoktur...
- Hook thread'inde programın uzun süre çıkmasını engelleyecek zaman alıcı işlemler gerçekleştirmeyin.
- Hook'ta 'exception' atmaktan kaçınmalısınız, aksi takdirde Java Sanal Makinesi'nden düzgün bir şekilde çıkamayabilirsiniz.
- Shutdown Hook'larının kayıt sırası çok önemlidir ve aralarındaki bağımlılık ilişkisine ve sıraya dikkat etmelisiniz; bu, gerçek duruma göre kararlaştırılmalıdır. Genellikle, önce daha basit bazı Shutdown Hook'ları kaydetmeli ve ardından daha karmaşık olanları kaydetmelisiniz.
- Shutdown Hook'unda yeni thread'ler başlatmamaya çalışın, aksi takdirde JVM düzgün bir şekilde kapanmayabilir!

#### Shutdown mekanizması çalışma prensibi

- Java'daki shutdown hook mekanizması JVM'deki iki thread'e dayanıyor. Main thread ve shutdown thread. Bir java uygulaması başladığında main thread bir shutdown hook oluşturur ve shutdown hook'larını hook listesine ekler
- JVM bir termination sinyali aldığında, önce tüm kullanıcı thread'lerini durdurur ve ardından shutdown thread başlatır. Shutdown thread, hook listesindeki sırayla her hook'u tek tek yürütür ve tüm hookların yürütülmesini veya zaman aşımına uğramasını bekler. Tüm hook'lar yürütülürse JVM normal şekilde exit olur, yoksa exit'e zorlar

#### Shutdown mekanizması source code analizi

1. Registration : `ApplicationShutdownHooks.add(hook);` çağırılır
2. Execution : `runHooks` metodu çağırılır ve listeye eklenen hook'lar çalıştırılır
3. Triggering shutdown hook :

- Java'da iki tür thread vardır, birisi kullanıcı thread'leri ve daemon thread'leri. Daemon thread'ler, GC thread gibi kullanıcı thread'lerine hizmet eder. JVM'in sonlanıp sonlanmayacağını belirlediği işaret hala çalışan kullanıcı thread'inin olup olmadığıdır. SOn kullancıı thread'i sona erdiğinde Shutdown.shutdown metodu çağırılacaktır. Bu JVM gibi sanal makine dillerine özgü bir ayrıcalıktır.

## Daemon thread nedir?

- Sistem eğer sonsuz döngüye bile girse daemon thread bu döngüyü bitirebilir.

```java
public static void main(String[] args) throws InterruptedException {
    Thread thread = new Thread(()-> {
       while (true){
           System.out.println("Daemon thread is running...!");
           try {
               Thread.sleep(2000L);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
    });


    thread.setDaemon(true);
    thread.start();
    Thread.sleep(1000);
    Runtime.getRuntime().addShutdownHook(new Thread(()-> System.out.println("Shutdown hook..!!")));
    System.out.println("The main thread is about to finish execution");
}
```

#### Rolü ve kullanım senaryoları

- Java'da daemon thread'lerinin tanıtmanın temel amacı, arka plan görevlerinin yürütülmesini desteklemek için bir mekanizma sağlamaktır. Daemon iş parçacıkları, programın yaşam döngüsünde yardımcı bir rol oynar ve diğer iş parçacıkları için destek ve hizmetler sağlar.
- Örnek olarak GC garbage collector thread'ini ele alalım. Bu klasik bir daemon thread'dir. Programımızda artık çalışan thread olmadığında, program artık çöp üretmeyecek ve çöp toplayıcının yapacak hiçbir şeyi kalmayacaktır. Bu nedenle, çöp toplama iş parçacığı JVM'de kalan tek iş parçacığı olduğunda, JVM otomatik olarak sonlanacaktır. Sistemdeki geri dönüştürülebilir kaynakları gerçek zamanlı olarak izlemek ve yönetmek için her zaman düşük bir seviyede çalışır.
  - Arkaplan taskları: log kaydetme, zamanlanmış işler, monitoring vs
  - Garbage collection: belleği boşaltmaktan sorumlu olan JVM'in önemli bir işlevidir. Garbage colelctor, program çalışma zamanı sırasında otomatik olarak yürütülen ve artık ihtiyaç duyulmayan nesneleri geri dönüştüren bir daemon thread'dir
  - Kaynak yönetimi: Boşta duran bağlantıları izleyip eğer bağlantı çok fazla süre boşlukta kalırsa kaynak kullanımını azaltmak için bağlantıyı otomatik olarak kapatabilir.
  - Daemon servisler: Örneğin, bir web sunucusunda, bir daemon thread istemci isteklerini dinleyebilir ve tüm istemci bağlantıları kesildiğinde, daemon thread sunucuyu otomatik olarak kapatabilir.

> Daemon bir thread'in sonlandırılması kontrol edilemez. O yüzden programda yalnızca daemon thread kldığında main thread'in sonlandırılmasıyla birlikte otomatik olarak sonlanacaktır. Bu nedenle daemon thread'i kullanırken görevin kesintiye uğratılabilir veya geri yüklenebilir olduğundan ve programın genel mantığını etkilemeyeceğinden emin olmak gerekir.

## Thread güvenlik konuları

#### Thread safety : Thread güvenliği

- Birden fazla thread bir nesneye eriştiğinde, nesneyi çağırma eylemi, çalışma zamanı ortamında bu thread'lerin zamanlaması ve dönüşümlü yürütülmesi dikkate alınmadan veya ek senkronizasyona gerek kalmadan doğru sonucu elde edebiliyorsa, nesne thread-safe'tir.
- Basit bir ifadeyle, bir işletmede bir nesneye veya bir metoda kaç thread erişirse erişsin, bu iş logic'i yazarken herhangi bir ek işleme gerek olmadığı (yani, tek thread'li bir program gibi programlanabileceği) ve programın normal şekilde çalışabileceği (çoklu thread nedeniyle başarısız olmayacağı) ve buna thread-safe denebileceği anlamına gelir.

#### Thread unsafe

- Birden fazla thread aynı anda bir nesneye eriştiğinde, bir thread nesnenin değerini güncellerken diğer thread aynı anda nesnenin değerini okuyorsa, yanlış bir değer elde etmek mümkündür. Bu durumda, bu bölümdeki kodun yürütülmesini senkronize etmek için `Synchronized` anahtar sözcüğünü kullanmak gibi doğru sonucu garantilemek için ek önlemler almamız gerekir.

#### Classification of thread safety issues

##### Incorrect running results

- Bir objeyi aynı anda birden fazla thread'in okuması ve yazması. (multithreading)
- Bunun nedeni, çoklu iş parçacığında CPU planlamasının zaman dilimleri birimlerine tahsis edilmesi ve her iş parçacığının belirli miktarda zaman dilimi alabilmesidir. Ancak, iş parçacığının sahip olduğu zaman dilimi tükenirse, askıya alınacak ve CPU kaynaklarını diğer iş parçacıklarına bırakacaktır; bu da iş parçacığı güvenliği sorunlarına yol açabilir. Örneğin, i++ işlemi yalnızca bir satır kod gibi görünür, ancak aslında atomik bir işlem değildir. Yürütme adımları esas olarak üç adıma bölünmüştür ve işlemin her adımı arasında kesintiye uğrayabilir.
- Bu sorunu çözmek için şu çözüme ihtiyacımız olabilir: Paylaşılan değişkenler üzerinde çalışan birden fazla thread olduğunda, aynı anda yalnızca bir threadnın bu değişken üzerinde çalıştığından ve diğer iş parçacıklarının threadnın verileri işlemeyi bitirmesini beklediğinden emin olmak gerekir. Bu yöntem, karşılıklı olarak münhasır erişim amacına ulaşabilen bir kilit olan mutex kilidi adı verilen bir araç kullanır. Yani, paylaşılan bir veri, şu anda ona erişen thread tarafından kilitlendiğinde, aynı anda, diğer iş parçacıkları yalnızca geçerli thread işlemeyi bitirip kilidi serbest bırakana kadar bekleme durumunda olabilir.

```java
    static int count;
public static void main(String[] args) throws InterruptedException {
  Runnable runnable = () -> {
    synchronized (IncorrectRunningResultDemo.class) {
      for (int i = 0; i < 10000; i++) {
        count++;
      }
    }
  };
  Thread thread1 = new Thread(runnable);
  Thread thread2 = new Thread(runnable);
  thread1.start();
  thread2.start();
  thread1.join();
  thread2.join();
  System.out.println(count);
}
```

##### Thread Liveness Problem

- Canlılık sorunları, programın hiçbir zaman çalışmanın nihai sonucunu alamaması gerçeğini ifade eder. Daha önce belirtilen hatalarla karşılaştırıldığında, canlılık sorunlarının sonuçları daha ciddi olabilir, örneğin programın tamamen takılıp daha fazla çalışamamasına neden olabilecek çıkmazlar.
- Örnek olarak bkz. : Thread Liveness Problem (Deadlock, Livelock and Starvation)

##### Security Issues During Object Publication and Initialization

- Son olarak, nesne yayımlama ve başlatma sırasında oluşan thread safety sorunları vardır. Nesneleri oluşturmak ve bunları diğer sınıflar veya nesneler tarafından kullanılmak üzere yayımlamak ve başlatmak yaygın bir işlemdir, ancak bunu yanlış zamanda veya yanlış yerde yaparsak, thread safety sorunlarına yol açabilir.
- Bir thread ile initialization yaptığımızda burda null pointer exception hatası alabiliriz, get yaparken hala dosya ya da liste, obje set edilmemiş olabilir.

#### In which scenarios do we need to pay extra attention to thread safety issues? : Hangi sernayoda extra dikkat etmeliyiz?

- **Accessing shared variables or resources** : statik değişkenlere, paylaşılan önbelleklere vb. erişim gibi paylaşılan değişkenlere veya paylaşılan kaynaklara erişim olduğunda ortaya çıkar. Bu bilgilere yalnızca bir thread tarafından erişilmeyecek, aynı anda birden fazla thread tarafından erişilmesi muhtemeldir. Bu durumda, daha önce bahsettiğimiz count++ örneği gibi thread güvenliği sorunları ortaya çıkabilir.
- **There is a binding relationship between different data** : Bazen farklı veriler gruplar halinde, birbirine karşılık gelen veya birbirine bağlı olarak görünür ve bunun en tipik örneği IP ve port numaralarıdır. Örneğin, IP'yi değiştirdiğimizde, genellikle port numarasını aynı anda değiştirmemiz gerekir. Bu iki işlem birbirine bağlı değilse, yalnızca IP veya port numarasının ayrı ayrı değiştirilmesi söz konusu olabilir. Bu sırada, bilgi serbest bırakıldıysa, bilgi alıcısı IP ve port numarasının yanlış bir bağlama sonucunu alabilir ve bu da iş parçacığı güvenliği sorunlarına neden olabilir. Bu durumda, işlemin atomisitesini de sağlamamız gerekir.
- **The class that is depended on does not declare itself to be thread-safe.** : Thread safe sınıflar bu konu ile alakalı bir beyanda bulunmuyorsa bu sınıflarla iş yaparken dikkat etmek gerekir

## Thread Liveness Problem (Deadlock, Livelock and Starvation)

##### Deadlock

- 2 thread'in birbirinin kaynaklarını beklemesi ancak aynı anda hiçbir thread'in elindeki kaynakları serbest bırakmaması ve bunun sonucunda da sonsuza dek bloke olması durumunda oluşan çıkmazdır.

```java
    static Object lock1 = new Object();
    static Object lock2 = new Object();

    public static void main(String[] args) {

        new Thread(()-> {
            try {
                synchronized (lock1){
                    Thread.sleep(500);
                    synchronized (lock2){
                        System.out.println("Thread 1 successfully started");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        new Thread(()-> {
            try {
                synchronized (lock2){
                    Thread.sleep(500);
                    synchronized (lock1){
                        System.out.println("Thread 1 successfully started");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
```

##### The necessary conditions for deadlock

- **Mutual exclusion condition** : Bir kaynak aynı anda yalnızca bir işlem veya iş parçacığı tarafından kullanılabilir. Örneğin, burada bir kilidim varsa, onu elde ettiğimde, diğer iş parçacıkları onu serbest bırakana kadar onu elde edemez.
- **Request and hold condition:** İlk iş parçacığı, ilk kilidi tutarken ikinci kilidi ister. Örneğin, iş parçacığı 1, isteğini yaparken bir B kilidi istemek ister, serbest bırakmadan daha önce edindiği A kilidini tutar.
- **Non-interference condition:** Kilit, harici müdahale tarafından mahrum bırakılmaz. Yani, çıkmazı sonlandırmak için harici bir müdahale yoktur.
- **Circular wait condition** : İki veya daha fazla iş parçacığı birbirini bekler veya dairesel olarak kilitlerin serbest bırakılmasını bekler. Örneğin, iki iş parçacığı koşulları bekliyor, yani, sen benim serbest bırakmamı bekliyorsun ve ben de senin serbest bırakmanı bekliyorum; birden fazla iş parçacığı koşulları bekliyor, yani, A, B'yi bekliyor, B, C'yi bekliyor, C, D'yi bekliyor, D, E'yi bekliyor, E, F'yi bekliyor, F, A'yı bekliyor ve serbest bırakmanın bir sonu yok, sonsuz bir bekleme, bir döngü oluşturuyor.

##### How to Prevent Deadlock

- **Obtain lock order** : lock'ların doğru sırasını iyice düşünmek gerekiyor, lock'lar birbirini bozmamalı.
- **Give up after timeout** :synchronized anahtar sözcüğü tarafından sağlanan yerleşik kilidi kullanırken, bir iş parçacığı kilidi elde etmediği sürece sonsuza kadar bekleyecektir, bu da istediğiniz etki olmayabilir. İstediğiniz şey, hala başarısız olursa bir süre bekledikten sonra vazgeçmektir. Bu durumda, Lock arayüzü tarafından sağlanan ve kilidi beklemek için belirli bir süre ayarlamanıza izin veren boolean tryLock(long time, TimeUnit unit) yöntemini kullanabilirsiniz.

#### Livelock

- Livelock, programın nihai sonucu asla almaması açısından bir 'deadlock'a çok benzer. İki veya daha fazla iş parçacığı yürütülürken, karşılıklı olarak kaynakları ertelerler, bunun sonucunda hiçbiri kaynakları elde edemez ve program normal şekilde çalışamaz. Ancak, aptalca yerinde beklemek yerine tekrar tekrar yürütmeyi denerler.
- Örnek olarak yolda karşılaşan iki insanın birbirine yol vermesi ve bu yol verme sonucunda da iki tarafın da yola devam edememesidir.

##### How to Prevent Livelock

- Bir programda canlı kilitlenmenin meydana gelmesinin nedeni aslında iki iş parçacığının aynı anda kilitleri edinip bırakmasıdır.
- Bu nedenle, canlı kilitlenmeyi önlemek için, bir kilit edinmeye çalışırken rastgele bir zamanı bekleyebiliriz. Bekleme süreleri farklı olduğundan, halihazırda tuttuğunuz kilidi serbest bırakma zamanlaması da farklı olacaktır. Bu nedenle, halihazırda tuttuğunuz kilidi serbest bıraktığınızda, başka bir iş parçacığı hala az önce serbest bıraktığınız kilidi edinmeye çalışıyor olabilir ve başarılı bir şekilde çalışarak çıkmazı kırabilir.
- Livelock'un tipik bir örneği mesajlaşma sistemindedir. İşlenmesi gereken çeşitli mesajların bulunduğu bir mesaj kuyruğunu hayal edin. Belirli bir anda, kuyruğa yanlış bir mesaj karışır. Yürütüldüğünde bir hata bildirir, ancak kuyruğun yeniden deneme mekanizması onu öncelikli yeniden deneme işlemi için kuyruğun başına geri koyar. Ancak, bu mesaj kaç kez yürütülürse yürütülsün, doğru bir şekilde işlenemez. Her hatadan sonra yeniden deneme kuyruğunun başına yerleştirilir ve bu döngü devam eder, sonuçta iş parçacığının her zaman meşgul olmasına neden olur, ancak program asla bir sonuç alamaz ve bu da bir livelock sorununa neden olur.
- Sorunu çözmenin iki yolu vardır:
  - Yürütülmeyi geciktirmek için hata mesajını doğrudan kuyruğa eklemek
  - Yeniden deneme sayısına bir sınır koymak. Çok fazla başarısız olan mesajlar atılabilir veya özel işleme için özel kuyruğa yerleştirilebilir.

#### Starvation

- Starvation, bir iş parçacığının her zaman belirli kaynakları, özellikle CPU kaynaklarını elde edememesi ve bunun sonucunda iş parçacığının çalışamaması ve sorunlara yol açması durumunu ifade eder
- Ortaya çıktğı senaryolar:
  - Bir iş parçacığının önceliği çok düşük ayarlanırsa, iş parçacığına hiçbir zaman CPU kaynakları tahsis edilmeyebilir ve bu nedenle uzun süre çalışamayabilir.
  - Bir iş parçacığı bir kilidi tutar ve kilidi serbest bırakmadan sonsuz bir döngüye girerse, diğer iş parçacıklarının uzun süre beklemesine neden olabilir; örneğin, bir program her zaman bir dosya için bir yazma kilidi işgal eder ve dosyayı değiştirmek isteyen diğer iş parçacıkları önce kilidi edinmelidir. Bu, dosyayı değiştirmek isteyen iş parçacıklarının açlığa düşmesine ve uzun süre çalışamamasına neden olabilir.

##### How to prevent starvation

- Kullanımdan sonra kilitlerin serbest bırakılmadığı programdaki mantıksal hatalara dikkat edin.
- Makul öncelikler belirleyin veya iş parçacıkları için öncelik belirlememeye çalışın.

## Deep Understanding of the Usage and Principles of the Synchronized Keyword

- Thread safety konularında bir önemli sorun da bir önemli kaynağa birden fazla thread'in aynı zamanda erişmesidir. Bunu engellemek için `synchronized` keyword'ü kullanılır. Bu keyword ile o alana sadece 1 thread aynı zamanda ulaşabilir.

#### Usage : three ways

- **Modifying a normal method**

```java
public int count = 0;

public synchronized void increase(){
    count++;
}

public static void main(String[] args) throws InterruptedException {
    InstanceSynchMethod instanceSynchMethod = new InstanceSynchMethod();
    Runnable runnable = () -> {
      for (int i = 0; i < 10000; i++){
          instanceSynchMethod.increase();
      }
    };

    Thread thread1 = new Thread(runnable);
    Thread thread2 = new Thread(runnable);
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();
    System.out.println("Count : " + instanceSynchMethod.count);
}
```

- **Modifying a static method**

```java
public static int count = 0;

public static synchronized void increase(){
    count++;
}
```

- **Modifying a code block**

```java
Runnable runnable = () -> {
    synchronized (instanceSynchMethod){
        for (int i = 0; i < 10000; i++){
            count++;
        }
    }
};
```

#### Semantic principle of `synchronized`

- Bir iş parçacığı synchronized bir kod bloğuna eriştiğinde, senkronize kodu yürütmeden önce ilk olarak monitör kilidini edinmesi gerekir. Çıkış yaparken veya bir istisna atarken, kilit serbest bırakılmalıdır.
- Semantic mantığı sycnhronized için `monitor` obkesi ile alakalıdır. wait/notify gibi metotlar da aynı şekilde monitor objesi ile çalışır.

#### Understanding Monitor

- Monitor sycnronization tool'u ya da senkronizasyon mekanizması olarak bilinir, aslında bir nesnedir. İşletim sisteminin monitor'ünden prensip olarak gelir ve ObjectMonitor da implemente edilmiş halidir.
- Daha önce Java'nın erken sürümlerinde senkronize olanın ağır sıklet kilitlere ait olduğunu ve verimsiz olduğunu belirtmiştik. Bunun nedeni, izleme kilidinin (monitör) uygulama için altta yatan işletim sisteminin Mutex Kilidi'ne güvenmesidir. İşletim sistemi iş parçacıkları arasında iş parçacığı geçişini uyguladığında, kullanıcı durumundan çekirdek durumuna geçmesi gerekir ve bu durumlar arasındaki geçiş nispeten uzun bir zaman gerektirir ve bu da nispeten yüksek bir zaman maliyetine neden olur. Erken senkronize olanın verimsiz olmasının nedeni de budur. Neyse ki, Java 6'dan sonra resmi Java, JVM düzeyinde senkronize olmak için önemli optimizasyonlar yaptı, bu nedenle mevcut senkronize kilidin verimliliği de çok iyi optimize edildi. Java 6'dan sonra, kilitleri elde etme ve serbest bırakmanın performans yükünü azaltmak için hafif kilitler ve önyargılı kilitler tanıtıldı.

## Optimization of JVM for Synchronized

- Lock mekanizması öncelikle mutex'e güveniyordu ve bundan dolayı da uzun zaman gerekiyordu. Bu durum düşük verimliliğe neden oluyordu. Geliştiriciler bu konu için ekstra çalışmalar yaparak bu kısımları optimize ettiler ve farklı lock mekanizmaları ortaya çıkarılmış oldu.

#### Biased Lock

- Aynı thread'in lock'u elde etme maliyetini azaltması. Çoğu durumda kilit için multi thread rekabeti yoktur ve her zaman aynı thread tarafından birden çok kez lock elde edilir. O durumda bu kilit biased lock'tur. Lock rekabeti yoksa biased lock iyi bir optimizasyon etkisine sahip olur

#### Lightweight Lock

- Biased lock başarısız olursa bu teknik denenecektir. İkinci bir thread aynı lock nesnesini istediğinde biased lock hemen bu lock'a yükseltilecektir.

#### Spin Lock

- Lightweight lock başarısız olursa buraya yükseltilir, sanal makine, iş parçacığının işletim sistemi düzeyinde gerçekten askıya alınmasını önlemek için spin lock adı verilen bir optimizasyon tekniği de gerçekleştirecektir. Bu, çoğu durumda, kilidi tutan iş parçacığının onu çok uzun süre tutmayacağı gerçeğine dayanmaktadır. İş parçacığı doğrudan işletim sistemi düzeyinde askıya alınırsa, işletim sisteminin kullanıcı modu ile çekirdek modu arasında iş parçacıklarını değiştirmesi gerektiği ve bu geçişin nispeten uzun bir zaman gerektirdiği ve nispeten yüksek bir zaman maliyetine sahip olduğu düşünüldüğünde, buna değmeyebilir. Bu nedenle, spin lock, yakın gelecekte geçerli iş parçacığının kilidi elde edebileceğini varsayar, bu nedenle sanal makine geçerli iş parçacığının birkaç boş döngü gerçekleştirmesine izin verir (bu aynı zamanda spin olarak adlandırılmasının nedenidir), genellikle çok uzun sürmez, belki 50 veya 100 döngü.

#### Adaptive Spin Lock

- Bir iş parçacığı başarıyla dönerse, bir sonraki seferki maksimum spin sayısı artacaktır, çünkü JVM, son seferde başarılı olduğu için bu sefer de başarılı olma olasılığının çok yüksek olduğuna inanmaktadır. Tersine, bir iş parçacığının başarılı bir şekilde dönmesi nadirse, bir sonraki seferki spin sayısı azaltılacak veya işlemci kaynaklarının israfını önlemek için spin işlemi atlanacaktır.

#### Lock Elimination

- Sanal makinenin bir başka tür kilit optimizasyonudur ve bu tür optimizasyon daha kapsamlıdır. Java sanal makinesi JIT derleme zamanında olduğunda (bu, derleme için belirli bir kodun ilk kez yürütülmek üzere olduğu, aynı zamanda tam zamanında derleme olarak da bilinir) çalışan bağlam taranarak, paylaşılan kaynak rekabeti olasılığının olmadığı kilidi kaldırılır. Bu şekilde, gereksiz kilitler ortadan kaldırılabilir ve anlamsız kilit isteme süresinden tasarruf edilebilir.

#### Lock Coarsening

- Sanal makinenin başka bir uç durum için optimizasyon işlemidir. Kilitlemenin kapsamını genişleterek, tekrarlanan kilitleme ve kilit açma işlemlerinden kaçınır.
- Bir grup çocuk, öğretmenlerinin liderliğinde eğlence parkına oynamaya gider. Eğlence parkının girişinde, her turistin içeri girmek için biletlerini göstermesi gerekir. Bilet kontrol görevlisi, bu çocuk grubunun kolektif olarak (veya bir takım olarak adlandırılır) geldiğini biliyorsa, bilet kontrol görevlisi bu çocuk grubunu bir bütün olarak (yani, kilit kabalaştırma) kabul edebilir ve her çocuğun biletini tek tek kontrol etmek yerine, tüm takımın biletlerini aynı anda kontrol edebilir. Bu şekilde bilet kontrol etme, süreci hızlandırmanın yanı sıra çocukların girişte bekleme süresini de azaltır.

## Java Memory Model

#### İki önemli soru

- Thread'ler nasıl haberleşir? Kendi aralarında bilgi aktarımını nasıl yaparlar?
- Thread'ler nasıl senkronize olurlar? Yani iş parçacıkları, farklı iş parçacıkları arasındaki işlemlerin göreceli sırasını hangi mekanizmayla kontrol eder?
- Burada iki concurrent model vardır :
  - Message-passing concurrent model : Communicate -> Thread'ler arasında ortak bir state yoktur ve haberleşme thread'ler arasında mesaj taşıyarak gerçekleşir. Senkronizasyon -> Bu model senkrondur çünkü mesaj göndermek almaktan önce gelir ve senkronizasyon örtüktür.
  - Shared-memory concurrent model : Communicate -> Thread'ler programın state'lerini ortak olarak paylaşır ve implicit olarak haberleşir, ortak state'e yazar ve okurlar. Senkronizasyon -> Mutual exclusive olması gerekir, senkronizasyon explicit şekilde olur.
- Java'da **shared-memory model** kullanılır.

### Java memory area

- Bir program çalıştırırken Java Virtual Machine otomatik şekilde memory'yi belirli alanlara böler ve bu alanları kontrol eder. Bu alanların hepsi kendine ait farklı özellikte ve görevdedirler. VM Stack, Native Method Stack, Program Counter Register  gibi alanlar tüm thread'ler tarafından paylaşılanları, Heap ve Method Area gibi alanlar da thread'lerin private data'larını gösterir.

#### Method area

- method alanı, iş parçacıkları tarafından paylaşılan bellek alanına aittir, ayrıca Non-Heap olarak da bilinir, esas olarak sınıf bilgilerini, sabitleri, statik değişkenleri, tam zamanında derleyici tarafından derlenen kodu ve sanal makine tarafından yüklenen diğer verileri depolamak için kullanılır. method alanı bellek ayırma gereksinimlerini karşılayamadığında, bir OutOfMemoryError istisnası atılır.
- method alanında, esas olarak derleyici tarafından oluşturulan çeşitli değişmezleri ve sembolik referansları depolamak için kullanılan Çalışma Zamanı Sabit Havuzu adlı bir alan olduğunu belirtmekte fayda var. Bu içerikler, sınıf yüklendikten sonra sonraki kullanım için çalışma zamanı sabit havuzunda depolanacaktır.
- Derleme süresi boyunca oluşturulan Sınıf dosyasının sabit havuzuna ek olarak, çalışma zamanı sabit havuzu çalışma zamanı sırasında sabit havuzuna yeni sabitler de ekleyebilir. Daha yaygın olanı String sınıfının intern() methodidir.
  - Literals: Similar to the concept of constants at the Java language level, including text strings, constant values declared as final, etc.
  - Symbolic references: Concepts at the compilation language level, including the following three categories:
  - Fully qualified names of classes and interfaces
  - Names and descriptors of fields
  - Names and descriptors of methods
- Constant pool :
  - Constant pool : Data compilation and class file'ın parçasıdır. Constant'ları method'ları ve interface gibi şeyleri tutar. String sabitlerini de tutar
  - String pool/String constant pool : Constant pool'un bir parçasıdır ve string-type dataları tutar
  - Runtime constant pool :: method area'nın bir parçasıdır ve tüm thread'ler tarafından paylaşılır. VM class'ı yükledikten sonra bu da constant pool'daki data'ları runtime constant pool'a yükler.

#### Java Heap

- Java heapı ayrıca iş parçacıkları tarafından paylaşılan bellek alanına aittir. Sanal makine başlatıldığında oluşturulur ve Java sanal makinesi tarafından yönetilen en büyük bellek parçasıdır. Esas olarak nesne örneklerini depolamak için kullanılır ve neredeyse tüm nesne örneklerine burada bellek tahsis edilir. Java heapı garbage collector tarafından yönetilen ana alan olduğundan, genellikle GC heapı olarak adlandırılır. heapda örnek tahsisini tamamlamak için bellek yoksa ve heap genişletilemiyorsa, bir OutOfMemoryError exception'u fırlatılır.

#### Program Counter Register

- Thread-private veri alanına aittir ve küçük bir bellek alanıdır, esas olarak geçerli thread tarafından yürütülen bayt kodu satır numarası göstergesini temsil eder. Bayt kodu yorumlayıcısı çalıştığında, bu sayacın değerini değiştirerek yürütülecek bir sonraki talimatı seçer. Branching, looping, jumping, exception handling ve thread recovery gibi temel işlevlerin tümü tamamlanmak için bu sayaca güvenir.
- Not: İş parçacığı bir Java methodu yürütüyorsa, bu sayaç yürütülen sanal makine bayt kodu talimatının adresini kaydeder; Yerel yöntem yürütülüyorsa, bu sayacın değeri boştur (Tanımsız).

#### Java Virtual Machine Stacks:

- Thread'e özel veri alanına aittir, iş parçacığıyla aynı anda oluşturulur ve toplam sayı iş parçacığıyla ilişkilendirilir ve Java yöntemlerinin yürütülmesi için bellek modelini temsil eder.
- **Local variable table:**
- **Operand Stack:**
- **Dynamic linking:**
- **Method exit**
- Exceptions : StackOverFlowError -> stack'in çok büyümesi, OutOfMemoryError -> Yeterli memory kalmaması

#### Overview of the java memory model

- Java Bellek Modeli aslında var olmayan soyut bir kavramdır. Bir dizi kural veya spesifikasyonu tanımlar. Bu spesifikasyon kümesi aracılığıyla, programdaki çeşitli değişkenlerin (örnek alanlar, statik alanlar ve dizi nesnelerini oluşturan öğeler dahil) erişim yöntemleri tanımlanır.
- Programı JVM'de çalıştıran varlık bir iş parçacığı olduğundan, her iş parçacığının iş parçacığına özel verileri depolamak için kendi çalışma belleği (yığın alanı olarak da adlandırılır) vardır. Java Bellek Modeli, tüm değişkenlerin tüm iş parçacıklarının erişebileceği paylaşılan bir bellek alanı olan ana bellekte olduğunu belirtir. İş parçacıkları ana bellekteki değişkenler üzerinde doğrudan işlem yapamaz, ancak önce değişkenleri ana bellekten kendi çalışma belleğine kopyalamalı, çalışma belleğindeki değişkenler üzerinde işlem yapmalı ve tamamlandıktan sonra bunları ana belleğe geri yazmalıdır. Çalışma belleği her thread için özel bir alan olduğundan farklı thread'ler birbirlerinin çalışma belleğine erişemez ve thread'ler arasındaki iletişim ana bellekten geçmelidir.

![img.png](src/main/resources/img.png)

- JMM ve Java bellek alanının bölünmesinin farklı bir kavramsal düzeyde olduğuna dikkat edilmelidir. Daha uygun olarak, JMM, programdaki paylaşılan veri alanındaki ve özel veri alanındaki çeşitli değişkenlerin erişim yöntemlerini kontrol eden bir dizi kuralı açıklar. JMM, atomiklik, düzenlilik ve görünürlük (daha sonra analiz edilecektir) etrafında döner.

#### Main memory

- Java instance objelerini depolar, Threadler tarafından oluşturulan tüm örnek nesneleri, örnek nesnelerin bir üye değişkeni veya bir metottaki local variable olup olmadığına bakılmaksızın ana bellekte depolanır ve elbette paylaşılan sınıf bilgileri, sabitler ve statik değişkenler de dahil edilir. Paylaşılan bir veri alanıdır, aynı değişkene erişen birden fazla thread thread-safety sorunu yaratabilir.

#### Working Memory

- Metodun tüm local variable bilgilerini depolar Tüm thread'ler kendi çalışma alanlarına erişebilir. Local veriler diğer thread'lerin erişmesine kapalıdır. Thread-safety sorunu yoktur.

#### Main - working memory connection

- Sanal makine spesifikasyonuna göre, bir örnek nesnesindeki bir üye yöntemi için, yöntem yerel değişkenler içeriyorsa ve temel bir veri türündeyse (boolean, byte, short, char, int, long, float, double), doğrudan çalışma belleğindeki yığın çerçeve yapısının yerel değişken tablosunda depolanacaktır.
- Yerel değişken bir referans türündeyse, nesnenin bellekteki belirli referans adresi çalışma belleğindeki yığın çerçeve yapısının yerel değişken tablosunda depolanacaktır; belirli örnek nesnesi ise ana bellekte (paylaşılan veri alanı: yığın) depolanacaktır.
- Kaynaklar
  - [JVM](https://javatutorial.net/jvm-explained/)
  - [JVM Architecture](https://www.geeksforgeeks.org/jvm-works-jvm-architecture/)

#### Hardware memory architecture

- CPU - Cache - RAM

#### Java thread implementation principle and the relationship between the operating system and java thread

- Hem Windows hem de Linux sistemlerinde, Java thread'lerinin uygulanması one-on-one thread modeline dayanır. Sözde one-on-one, aslında dil düzeyindeki programlar aracılığıyla sistem çekirdeğinin iş parçacığı modeline dolaylı bir çağrıdır. Yani, Java iş parçacıklarını kullandığımızda, Java sanal makinesi dahili olarak geçerli görevi tamamlamak için geçerli işletim sisteminin çekirdek iş parçacıklarını çağırmaya yönelir.
- Burada, işletim sistemi çekirdeği tarafından desteklenen bir thread olan Kernel-Level Thread (KLT) terimini anlamak gerekir. Bu tür iş parçacıkları işletim sistemi çekirdeği tarafından değiştirilir. kernel, işlem zamanlayıcısı aracılığıyla iş parçacıklarının zamanlamasını gerçekleştirir ve iş parçacıklarının görevlerini her işlemciye eşler. Her kernel thread, çekirdeğin bir enkarnasyonu olarak düşünülebilir; bu nedenle işletim sistemi aynı anda birden fazla görevi işleyebilir. Yazdığımız çok iş parçacıklı programlar dil düzeyine ait olduğundan, programlar genellikle kernel iş parçacıklarını doğrudan çağırmaz. Bunun yerine, olağan anlamda bir thread olan Hafif Ağırlık İşlemidir. Her lightweight process bir kernel threadna eşlendiğinden, kernel threadnı lightweight process aracılığıyla çağırabiliriz ve ardından işletim sistemi çekirdeği görevleri her işlemciye eşler. lightweight processler ve kernel iş parçacıkları arasındaki bu bire bir ilişkiye bire bir thread modeli denir.
- Java'da her thread işletim sistemi kernel'inden geçecek ve ardından işleme için CPU'ya eşlenecektir. CPU çok çekirdekliyse, o zaman bir CPU aynı zamanda birden fazla thread'i paralel olarak planlayabillir ve yürütülebilir.

#### Relationship between java memory model and hardware memory architecture

- Donanım belleği için yalnızca kayıtlar, önbellek ve ana bellek kavramları vardır ve çalışma belleği (iş parçacığı özel veri alanı) ile ana bellek (iş parçacığı paylaşımlı veri alanı) arasında bir ayrım yoktur. Yani, JMM'nin bellek bölümü donanım belleği üzerinde hiçbir etkiye sahip olamaz, çünkü JMM yalnızca soyut bir kavram ve bir dizi kuraldır ve gerçekte var değildir.Çalışma belleğinin veya ana belleğin verileri olsun, bilgisayar donanımı için bilgisayar ana belleğinde, CPU önbelleğinde veya kayıtta saklanabilir.
- Bu nedenle Java memory model ve bilgisayar donanım bellek mimarisi soyut bir kavram bölümü ile gerçek fiziksel donanımı kesişen bir ilişkiye sahiptir.

#### Why is the java memory model needed?

- JVM'nin programı çalıştırdığı varlık bir iş parçacığı olduğundan ve her iş parçacığı oluşturulduğunda JVM, iş parçacığına özel verileri depolamak için bir çalışma belleği (bazı yerlerde yığın alanı olarak adlandırılır) oluşturur.
- İş parçacığının birincil bellekteki değişkenler üzerindeki işlemleri, çalışma belleği aracılığıyla dolaylı olarak tamamlanmalıdır. Ana işlem, değişkenleri birincil bellekten her iş parçacığının ilgili çalışma belleği alanına kopyalamak, ardından değişkenler üzerinde işlem yapmak ve işlem tamamlandıktan sonra değişkenleri birincil belleğe geri yazmaktır. Birincil bellekteki bir örnek nesnesinin değişkenleri üzerinde aynı anda işlem yapan iki iş parçacığı varsa, bu iş parçacığı güvenliği sorunlarına yol açabilir.

#### Three major characteristics in the java memory model

##### Atomicity

- Atomicity, bir işlemin kesintiye uğramaması anlamına gelir. Çok iş parçacıklı bir ortamda bile, bir işlem başladıktan sonra diğer iş parçacıklarından etkilenmez.
- Örneğin, int count = 0 statik değişkeni için, iki iş parçacığı ona aynı anda değer atarsa, iş parçacığı A count = 1 ve iş parçacığı B count = 2 yaparsa, iş parçacıkları nasıl çalışırsa çalışsın, count'un son değeri 1 veya 2 olur. A ve B iş parçacıklarının işlemleri birbirleriyle çakışmaz. Bu, kesintiye uğramama özelliğine sahip atomik bir işlemdir.
- Aslında, atomik bir işlemin özü, bir işlem kümesinin ya hepsinin başarılı olması ya da hepsinin başarısız olmasıdır.

##### Visibility

- Eğer instruction reordering olgusunu gerçekten anlarsanız, görünürlüğü anlamanız çok kolay olacaktır. Görünürlük şu anlama gelir: Bir iş parçacığı belirli bir paylaşılan değişkenin değerini değiştirdiğinde, diğer iş parçacıkları bu değiştirilmiş değeri hemen bilebilir mi?
- Ek olarak, talimat yeniden düzenleme ve derleyici optimizasyonu da görünürlük sorunlarına yol açabilir. Önceki analizden biliyoruz ki: İster derleyici optimizasyonunun yeniden düzenleme fenomeni olsun ister işlemci optimizasyonu, çok iş parçacıklı bir ortamda, programın sırasız yürütülmesine yol açacak ve dolayısıyla görünürlük sorunlarına da yol açacaktır.

##### Orderliness

- Düzenlilik, tek bir iş parçacığının yürütme kodunu ifade eder. Kodun yürütülmesinin her zaman birbiri ardına sırayla gerçekleştirildiğini düşünürüz. Böyle bir anlayış yanlış değildir. Sonuçta, bu tek bir iş parçacığı için gerçekten de geçerlidir. Ancak çok iş parçacıklı bir ortamda, program makine kodu talimatlarına derlendikten sonra talimat yeniden sıralaması gerçekleşebileceğinden düzensizlik meydana gelebilir.
- Yeniden sıralanan talimatlar orijinal talimatlarla aynı sırada olmayabilir. Bir Java programında, geçerli iş parçacığının içindeyse, tüm işlemlerin sıralı davranışlar olarak kabul edildiği anlaşılmalıdır. Çok iş parçacıklı bir ortamda, bir iş parçacığı başka bir iş parçacığını gözlemlediğinde, tüm işlemler düzensizdir. Cümlenin ilk yarısı tek bir iş parçacığı içinde seri semantik yürütmenin tutarlılığını sağlamaktan bahseder ve ikinci yarısı talimat yeniden sıralaması olgusundan ve çalışma belleği ile ana bellek arasındaki senkronizasyon gecikmesinden bahseder.

#### Solutions provided by JMM

- Atomiklik sorunu için, JVM tarafından sağlanan temel veri tipleri üzerindeki okuma ve yazma işlemlerinin atomiklik özelliğine ek olarak, metot düzeyinde veya kod bloğu düzeyindeki atomik işlemler için, program yürütmenin atomiklik özelliğini sağlamak amacıyla synchronized anahtar sözcüğü veya ReentrantLock kullanılabilir.
- Çalışma belleği ile ana bellek arasındaki senkronizasyon gecikmesi olgusunun neden olduğu görünürlük sorunu için, synchronized anahtar sözcüğü veya volatile anahtar sözcüğü bu sorunu çözmek için kullanılabilir. Her ikisi de bir iş parçacığı tarafından değiştirilen değişkenleri diğer iş parçacıkları için anında görünür hale getirebilir.
- Talimat yeniden düzenlemesinin neden olduğu görünürlük ve düzenlilik sorunları için, volatile anahtar sözcüğü bunları çözmek için kullanılabilir çünkü volatile'ın bir diğer işlevi de yeniden düzenleme optimizasyonunu engellemektir. volatile anahtar sözcüğü daha sonra daha ayrıntılı olarak analiz edilecektir. Atomikliği, görünürlüğü ve düzenliliği sağlamak için synchronized ve volatile anahtar sözcüklerine güvenmenin yanı sıra, JMM ayrıca çok iş parçacıklı bir ortamda iki işlem arasında atomikliği, görünürlüğü ve düzenliliği sağlamak için bir dizi happen-before ilkesini dahili olarak tanımlar.

#### Understanding the happens-before principle in JMM

- Program order rule : semantic seriality, yani kodun sırayla çalışması
- Lock rule : Unlock işlemi lock işleminden sonra olur, Yani, bir kilit açılıp daha sonra tekrar kilitlendiğinde, kilitleme eyleminin (aynı kilit için) açma eyleminden sonra gerçekleşmesi gerekir
- Volatile rule : Uçucu bir değişkenin yazılması, okumadan önce gerçekleşir. Bu, uçucu değişkenin görünürlüğünü garanti eder. Basit bir anlayış, uçucu bir değişkene bir iş parçacığı tarafından her erişildiğinde, bu değişkenin değerini ana bellekten okumaya zorlanmasıdır. Ve bu değişken değiştiğinde, en son değeri ana belleğe yenilemeye zorlanır. Herhangi bir anda, farklı iş parçacıkları her zaman bu değişkenin en son değerini görebilir
- Thread start rule : Bir thread'in start() metodu, her bir eylemden önce çalışır. Yani bir thread A, thread B'nin start metodunu yürütmeden önce paylaşılan bir değişkenin değerini değiştirirse, thread B start metodunu yürüttüğünde, thread A tarafından değiştirilen shared variable thread B tarafından görülebilir.
- Transitivity Priority Rule : Thread'ler için, A, B'den önce geliyorsa ve B de C'den önce geliyorsa o zaman A, C'den önce gelmelidir.
- Thread Termination Rule : Bir thread'in tüm işlemleri thread'in sonlanmasından önce gerçekleşir. Thread.join() metodunun işlevi, şu anda yürütülen thread'in sonlanmasını beklemektir. Thread B sonlanmadan önce, paylaşılan değişkenin değiştirildiğini varsayalım. Thread A, thread B'nin birleştirme metodundan başarıyla döndükten sonra thread B tarafından paylaşılan değişkenin değiştirilmesi thread A tarafından görülebilir.
- Thread Interruption Rule : Bir iş parçacığının interrupt() yöntemine yapılan çağrı, kesintiye uğrayan iş parçacığının kodunun kesinti olayının oluşumunu algılamasından önce gerçekleşir. Bir iş parçacığının kesintiye uğrayıp uğramadığı Thread.interrupted() yöntemi tarafından algılanabilir
- Object termination rule : Bir nesnenin constructor'ının yürütülmesi ve sonlandırılması finalize() metodundan önce gelir.

## Understanding the `volatile` keyword

- Volatile, Java Sanal Makinesi tarafından sağlanan hafif bir senkronizasyon mekanizmasıdır ve eş zamanlı programlamada sıklıkla karşılaşırsınız.
  - Bir thread volatile tarafından değiştirilen bir değişken üzerinde değişiklik işlemi gerçekleştirdiğinde bu her zaman diğer thread'ler tarafından görülebilir. 
  - Instruction reordering optimizasyonunu kısıtlar
- Shared variable'ı değiştirebilmek için volatile kullandığımızda garanti edebileceğimiz şey şudur : bir thread tarafından değiştirilen bir değişken üzerinde bir değişiklik işlemi gerçekleştirildiğinde bu her zaman diğer thread'ler tarafından görülebilir.
```java
public static volatile int i = 0;

public static void add(){
    i++;
}
```
Burda sorun hala vardır, i değişkeni diğer thread'ler tarafından görülebilir ama hala thread-safety problemleri çekilmektedir. Çünkü add metodu atomic bir operasyon değildir.
  - i değerini bellekte register'a kaydeder
  - Register'ı 1 artırır
  - Register'daki artırılmış değeri tekrar memory'ye yazar
- Başka thread'ler tarafından bölünme mümkündür. Bu nedenle thread-safety sorunu hala devam eder.
  Bu nedenle, birden fazla iş parçacığının bu anda add() yöntemini çağırması durumunda iş parçacığı güvenliği sorunlarının yine de ortaya çıkacağını netleştirmemiz gerekir. Bu sorunu çözmek istiyorsak, yine de sağlamak için senkronize, kilit veya atomik sınıfları kullanmamız gerekir. Volatile anahtar sözcüğü yalnızca talimat yeniden sıralamasını yasaklayabilir ve görünürlüğü sağlayabilir.
```java
public static synchronized void add(){
    i++;
}
```

#### How does `volatile` prevent instruction reordering?
- https://jenkov.com/tutorials/java-concurrency/java-happens-before-guarantee.html
- https://amanda-ariyaratne.medium.com/how-program-reordering-breaks-double-checked-locking-in-java-2756db827523

> #### Instruction reordering
> 

- volatile anahtar sözcüğünün bir diğer işlevi, derleyicinin veya işlemcinin talimatları yeniden düzenlemesini ve optimize etmesini önlemek ve böylece çok iş parçacıklı bir ortamda programın düzensiz yürütülmesi olgusunu önlemektir. Peki volatile, talimat yeniden düzenleme optimizasyonunun önlenmesini nasıl sağlar? Önce bir kavram olan Memory Barrier'i anlayalım.
- Memory barrier (bellek çiti olarak da bilinir) bir CPU talimatıdır. İki işlevi vardır. Biri belirli işlemlerin yürütme sırasını sağlamak, diğeri ise belirli değişkenlerin bellek görünürlüğünü sağlamaktır (yani, volatile'ın bellek görünürlüğü bu özellik kullanılarak elde edilir).
- Hem derleyici hem de işlemci talimat yeniden sıralama optimizasyonu gerçekleştirebildiğinden, talimatlar arasına bir MemoryBarrier eklenirse, derleyiciye ve CPU'ya, türü ne olursa olsun hiçbir talimatın bu MemoryBarrier talimatıyla yeniden sıralanamayacağını söyler. Yani, bir bellek bariyeri eklenerek, bellek bariyeri içindeki talimat yeniden sıralama optimizasyonu engellenir.
- Bellek Bariyerinin bir diğer işlevi: Çeşitli CPU önbellek verilerinin temizlenmesini zorlar, böylece herhangi bir CPU'daki iş parçacıkları bu verilerin en son sürümünü okuyabilir.
- Sonuç olarak, uçucu değişkenler, bellekteki semantiklerine, yani görünürlüğe ve yeniden sıralama optimizasyonunun önlenmesine, tam olarak bellek bariyerleri aracılığıyla ulaşırlar.

```java
public class DoubleCheckSingleton {

    private static Singleton singleton;

    private DoubleCheckSingleton(){
    }

    public static Singleton getInstance(){
        if (singleton == null){
            synchronized (Singleton.class){
                if (singleton == null){
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }

}
```
Bu kod klasik double-check singleton paternidir. Bu kodun single-threadded ortamda bir sorunu olmaz. Ama multi-threadded ortamda birkaç thread-safety konuları ile karşılaşabiliriz. Sebep olarak bir thread ilk check'i yapıp singleton'ın null olmadığını görünce referans obje initialize edilemeyecek.

- Ancak, instruction reordering yalnızca seri semantik yürütmenin tutarlılığını (tek bir iş parçacığında) garanti eder ve birden fazla iş parçacığı arasındaki semantik tutarlılığı önemsemez. Bu nedenle, bir iş parçacığı singleton'a eriştiğinde ve null olmadığında, singleton örneği tamamen başlatılmamış olabileceğinden, iş parçacığı güvenliği sorunlarına neden olur.

## Unsafe class

```java
public class UnsafeDemo {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);

        Unsafe unsafe = (Unsafe) field.get(null);
        Student student = (Student) unsafe.allocateInstance(Student.class);
        Class studentClass = student.getClass();
        Field name = studentClass.getField("name");
        Field age = studentClass.getField("age");
        Field school = studentClass.getDeclaredField("school");

        unsafe.putInt(student, unsafe.objectFieldOffset(age), 19);
        unsafe.putObject(student, unsafe.objectFieldOffset(name), "Test");
        Object schoolFieldBase = unsafe.staticFieldBase(school);
        System.out.println("staticFieldOffset:" + schoolFieldBase);

        long data = 2000;
        byte size = 1;
        long memoryAddress = unsafe.allocateMemory(size);
        unsafe.putAddress(memoryAddress, data);

        long addrData = unsafe.getAddress(memoryAddress);
        System.out.println("Addr Data : "+ addrData);

    }
}

class Student{
    public Student() {
    }
    private String name;
    private int age;
    private static String school = "Stanford University";
    @Override
    public String toString() {
        return super.toString();
    }
}
```

#### Thread Operations: Provide Support for Thread Suspension and Resume Operations
- Bir iş parçacığının askıya alınması park metoduyla gerçekleştirilir. Park çağrıldıktan sonra, iş parçacığı zaman aşımı veya kesinti gibi koşullar oluşana kadar engellenir. unpark askıya alınmış bir iş parçacığını sonlandırabilir ve normale döndürebilir. Java'daki iş parçacıklarının askıya alma işlemi LockSupport sınıfında kapsüllenmiştir. LockSupport sınıfında park metodunun çeşitli versiyonları vardır ve temel uygulaması nihayetinde Unsafe.park() metodunu ve Unsafe.unpark() metodunu kullanır.

#### Memory Barrier Support : avoid code reordering
- Burada esas olarak loadFence, storeFence, fullFence vb. gibi yöntemler yer alır. Bu yöntemler Java 8'de yeni tanıtılmıştır ve kod yeniden düzenlenmesini önlemek için bellek bariyerlerini tanımlamak için kullanılır. Java Bellek Modeli ile ilişkilidirler.
- `loadFence` : bu metodun çağırılması önceki tüm okuma işlemlerinin yürütülmesinden önce tamamlandığından emin olur. Bu CPU veya derleyicinin okuma işlemlerini yeniden düzenlemesini önleyebililr. Böylece programın geliştirici tarafından belirlenen sırayla yürütülmesini sağlar, `storeFence` : Bu metodun yürütülmesinden önceki tüm yazma işlemlerinin tamamlandığından emin olur , `fullFence` : Bu metot çağırılırsa önceki tüm okuma ve yazma işlemlerinin tamamlandığından emin olur.


## The Principles and Usage of Atomic Operation Classes in the JUC Package

- Basic type atomic operation classes, reference type atomic op class, array type atomic op class, attribute update atomic op class


#### Basic type atomic operation classes
- AtomicInteger, AtomicBoolean, and AtomicLong

```java
  public final int getAndDecrement() {
      return unsafe.getAndAddInt(this, valueOffset, -1);
  }
 // The current value increments by delta and returns the old value. The underlying CAS operation
  public final int getAndAdd(int delta) {
      return unsafe.getAndAddInt(this, valueOffset, delta);
  }
  // The current value increments by 1 and returns the incremented new value. The underlying CAS operation
  public final int incrementAndGet() {
      return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
  }
  // The current value decrements by 1 and returns the decremented new value. The underlying CAS operation
  public final int decrementAndGet() {
      return unsafe.getAndAddInt(this, valueOffset, -1) - 1;
  }
 // The current value increments by delta and returns the new value. The underlying CAS operation
  public final int addAndGet(int delta) {
      return unsafe.getAndAddInt(this, valueOffset, delta) + delta;
  }
```

- Example :
```java
static AtomicInteger atomicInteger = new AtomicInteger();
public static class AddThread implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++){
            atomicInteger.incrementAndGet();
        }
    }
}
```

#### Atomic Operation Classes of Reference Types

- Example:

```java
    public static AtomicReference<Student> studentAtomicReference = new AtomicReference<>();

    public static void main(String[] args) {
        Student s1 = new Student("Tom", 10);
        studentAtomicReference.set(s1);


        Student s2 = new Student("Jerry", 12);
        studentAtomicReference.set(s2);

        studentAtomicReference.compareAndSet(s1, s2);

        System.out.println(studentAtomicReference.get().toString());

    }

    static class Student{
        private String name;
        private int age;

        public Student(String name, int age){
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
```

```
getAndUpdate(UnaryOperator)
updateAndGet(UnaryOperator)
getAndAccumulate(V, AnaryOperator)
accumulateAndGet(V, AnaryOperator)
```

#### Atomic Operation Classes of Array Types

```
AtomicIntegerArray
AtomicLongArray
AtomicReferenceArray
```


```java
static AtomicIntegerArray atomicArr = new AtomicIntegerArray(10);

public static class IncrementTask implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++){
            atomicArr.getAndIncrement(i % atomicArr.length());
        }
    }
}

public static void main(String[] args) throws InterruptedException {
    Thread[] threads = new Thread[10];

    for (int i = 0; i < 10; i++){
        threads[i] = new Thread(new IncrementTask());
    }

    for (int i = 0; i < 10; i++){
        threads[i].start();
    }

    for (int i = 0; i < 10; i++){
        threads[i].join();
    }

    System.out.println(atomicArr);

}
```

#### Atomic Operation Classes for Attribute Updates

```
AtomicIntegerFieldUpdater
AtomicLongFieldUpdater
AtomicReferenceFieldUpdater
```

- Example :
```java
    public static class Candidate{
        int id;
        volatile int score;
    }

    static AtomicIntegerFieldUpdater<Candidate> atomicIntegerFieldUpdater =
            AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");

    static AtomicReferenceFieldUpdater<Student, String> atRefUpdate =
            AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");

    public static AtomicInteger allScore = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final Candidate stu = new Candidate();
        Thread[] t = new Thread[10000];
        for (int i = 0; i < 10000; i++){
            t[i] = new Thread(){
              public void run(){
                  if (Math.random() > 0.5){
                      atomicIntegerFieldUpdater.incrementAndGet(stu);
                      allScore.incrementAndGet();
                  }
              }
            };
            t[i].start();
        }

        for (int i = 0; i < 10000; i++){
            t[i].join();
        }

        System.out.println("final score=" + stu.score);
        System.out.println("check all score=" + allScore);

        //AtomicReferenceFieldUpdater simple use
        Student student = new Student("Jerry", 17);
        atRefUpdate.compareAndSet(student, student.name, "Jerry-1");
        System.out.println(student);

    }

    static class Student {
        private String name;
        private int age;

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
```

## Problems existing in CAS

## Lock interface & ReentrantLock

- `synchronized` keyword'ü kullanırken bir kilit mekanizması kurmamıza gerek yoktur, çünkü burda kilidi otomatik olarak kurmuş oluruz. Ama lock interface'i kullanırken bu kilidi ve kilidi serbest bırakma işlemlerini kendimizin yapması gerekir.
- Lock nesnesinin, synchronized'ın sahip olmadığı diğer senkronizasyon özelliklerini de sağladığı görülebilir, örneğin kilitlerin kesintiye uğrayabilir şekilde edinilmesi (syncrous, bir kilidi edinmeyi beklerken kesintiye uğramaz), zaman aşımından sonra kilitlerin edinilmesinin kesintiye uğraması ve bekleme ve uyandırma mekanizması için birden fazla koşullu değişken Condition. Bu ayrıca Lock'u kullanımda daha esnek hale getirir.

#### ReentrantLock

- ReentrantLock, JDK 1.5'te eklenen bir sınıftır. Lock arayüzünü uygular ve synchronized anahtar sözcüğüne benzer bir etkiye sahiptir, ancak synchronized'dan daha esnektir. ReentrantLock'un kendisi de bir reentrant kilididir, yani bu kilit kaynakları tekrar tekrar kilitlemek için bir iş parçacığını destekleyebilir. Aynı zamanda, adil ve adil olmayan kilitleri de destekler.
- Sözde adalet ve adaletsizlik, isteklerin sırasına atıfta bulunur. Kilidi ilk talep eden kişi önce kilidi elde etmek zorundaysa, bu adil bir kilittir. Tersine, kilidi elde etmek için kronolojik bir sıra yoksa, örneğin, daha sonra talep eden bir iş parçacığı kilidi önce elde edebilir. Bu adil olmayan bir kilittir.
- ReentrantLock'un aynı iş parçacığı tarafından yeniden kilitlemeyi desteklediği unutulmamalıdır. Ancak, kilit ne kadar çok kilitlenirse kilit o kadar çok kez açılmalıdır ki kilit başarıyla serbest bırakılabilsin. ReentrantLock'un basit bir kullanım örneğine bir göz atalım:

#### how should we choose between ReentrantLock and synchronized in actual scenarios?

- `synchronized` kullanımı nispeten daha rahattır ve daha net semantiği vardır. Aynı zamanda, JVM de bunu bizim için otomatik olarak optimize eder. ReetrantLock kullanımı daha esnektir ve ayrıca kilit edinimi için zaman aşımı kesintisi, kesilebilir kilit edinimi, bekleme ve uyandırma mekanizması için birden fazla koşullu değişken (Koşul) vb. gibi çeşitlendirilmiş destek sağlar. Bu nedenle bu işlevlere ihtiyaç duyduğumuzda ReetrantLock'u seçebiliriz. Ancak hangisinin özellikle kullanılacağı yine de iş gereksinimlerine göre belirlenmelidir.
- Örnek olarak 1-2 arasında trafik yoğunsa bu durumda hangi kilit daha uygun olacaktır? Cevap olarak ReentrantLock. 
- Neden? Çünkü synchronized ile ilgili önceki yazıda, synchronized'ın kilit yükseltme/şişirme işleminin geri döndürülemez olduğunu ve bir Java programının çalışması sırasında kilit düşürme durumunun neredeyse hiç olmayacağını belirtmiştik. Bu iş senaryosunda, trafiğin keskin bir şekilde arttığı dönemde, synchronized'ın doğrudan ağır bir kilide genişlemesi mümkündür. Synchronized ağır bir kilide yükseltildikten sonra, kilidin her sonraki edinimi ağır bir kilit olacaktır ve bu da programın sonraki performansı üzerinde daha büyük bir etkiye sahiptir.


