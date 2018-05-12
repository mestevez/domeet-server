package rx;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RXHelloWorld {

	@Test
	void helloWorld() {
		Flowable.just("Hello world").subscribe(System.out::println);
	}

	@Test
	void backgroundExecution() throws InterruptedException {
		Flowable.fromCallable(() -> {
			Thread.sleep(1000); //  imitate expensive computation
			return "Done";
		})
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.single())
				.subscribe(System.out::println, Throwable::printStackTrace);

		Thread.sleep(2000); // <--- wait for the flow to finish
	}

	@Test
	void classAttributeChangingPrint() throws InterruptedException {
		Person2 john = new Person2("John");

		john.getObservable().subscribe(System.out::println);

		Thread.sleep(2000); // <--- wait for the flow to finish

		john.setName("Michael");
		john.setName("Lucia");
	}

	@Test
	void classAttributeChangingSet() throws InterruptedException {
		Map<String, String> options = new HashMap<>();
		Person2 john = new Person2("John");

		john.getObservable().subscribe((p) -> {
			options.put("name", p.name);
		});
		Thread.sleep(100); // <--- wait for subscribe take action

		john.setName("Michael");
		john.setName("Lucia");

		Thread.sleep(100); // <--- wait for publish take action

		Assertions.assertEquals("Lucia", options.get("name"));
	}

	@Test
	void multipleSubscribers() throws InterruptedException {
		Map<String, String> options = new HashMap<>();
		Person2 john = new Person2("John");

		john.getObservable().subscribe((p) -> {
			options.put("name1", p.name);
		});
		john.getObservable().subscribe((p) -> {
			options.put("name2", p.name);
		});
		Thread.sleep(100); // <--- wait for subscribe take action

		john.setName("Michael");
		john.setName("Lucia");

		Thread.sleep(100); // <--- wait for publish take action

		Assertions.assertEquals("Lucia", options.get("name1"));
		Assertions.assertEquals("Lucia", options.get("name2"));
	}

	@Test
	void subscriberDispose() throws InterruptedException {
		Map<String, String> options = new HashMap<>();
		Person2 john = new Person2("John");

		new Thread(() -> {
			try {
				Thread.sleep(1000); // <--- wait for subscribe take action

				john.setName("Michael");

				Thread.sleep(1000); // <--- wait for subscribe take action

				john.setName("Lucia");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();


		new Thread(() -> {
			try {
				Disposable name = john.getObservable().subscribe((p) -> {
					options.put("name", p.name);
				});

				Thread.sleep(1500); // <--- wait for subscribe take action

				name.dispose();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		Thread.sleep(3000); // <--- wait for the flow to finish

		Assertions.assertEquals("Michael", options.get("name"));
	}

	class Person2 {
		String name;

		// Java RX
		Subject<Person2> mObservable = PublishSubject.create();

		Person2(String name) {
			this.name = name;
		}

		@Override
		public java.lang.String toString() {
			return "Name: " + this.name;
		}

		void setName(String name) {
			this.name = name;
			mObservable.onNext(this);
		}

		Subject<Person2> getObservable() {
			return mObservable;
		}
	}
}
