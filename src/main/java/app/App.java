package app;

public class App {
  public static void main(String[] args) {
    Flowable<String> flowable = Flowable.create(new FlowableONSubscribe<String>() {
      @Override
      public void subscribe(FlowableEmitter<String> emitter) throws Exception {
        String[] data = {"data1", "data2"};
        for(String item : data) {
          if (emitter.isCancelled()) {
            return;
          }
          emitter.onNext(item);
        }
        emitter.onComplete(); 
      }
    }, BackpressureStrategy.BUFFER);

    flowable.observeOn(Schedulers.computation())
            .subscribe(new Subscriber<String>() {
              private Subscription subscription;

              @Override
              public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1L);
              }

              @Override
              public void onNext(String data) {
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " : " + data);
                this.subscription.request(1L);
              }

              @Override
              public void onComplete() {
                String threadName = Thread.currentThread().getName();
                System.out.println(htreadName + " : DONE");
              }
              
              @Override
              public void onError(Throwable error) {
                error.printStackTrace();
              }
          });
    Thread.sleep(500L);
  }
}
