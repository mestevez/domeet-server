package model;

import conf.database.JUnitDatabaseProps;
import hibernate.SessionFactoryProvider;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class test_user {

	private static SessionFactory sessionFactory;

	@BeforeAll
	static void beforeAll() {
		sessionFactory = SessionFactoryProvider.getSessionFactory(JUnitDatabaseProps.getDatabaseProps());

		Session session = sessionFactory.openSession();
		try {
			session.beginTransaction();

			session.createQuery("DELETE FROM auth_user WHERE user_id > 50").executeUpdate();

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		} finally {
			session.close();
		}
	}

	@Test
	void addUser() {
		//String example = "SGVsbG8gV29ybGQ=";
		String example = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAgEAyADIAAD/wAARCAIoA3ADASIAAhEBAxEB/9sAhAAFAwMEAwMFBAQEBQUFBgcNCAcHBwcQCwwJDRMQFBMSEBISFRceGRUWHBYSEhojGhwfICEiIRQZJSckICceISEgAQUFBQcGBw8ICA8gFRIVFSAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICD/xAGiAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgsQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+gEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoLEQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/APe6KKK9I5QooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAClh/wBdH/vUlLD/AK6P/eoASiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAClh/10f+9SUsP+uj/wB6gBKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKWH/XR/wC9SUsP+uj/AN6gBKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKWH/XR/71JSw/66P/eoASiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAClh/wBdH/vUlLD/AK6P/eoASiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKfDDJO4jiRnY9AozSAZQB2ArodN8FzzAPeP5K/3V5NdBY6BYafgxQKWH8TcmspVox2LUGzjbPQdQvQGitmC+rcCtGDwRdvjzZ4ox7ZJrsAAOAKKxdeT2LVNHOw+BrVV/e3ErH/ZwKk/4Qiw/wCe1x+Y/wAK3qKj2ku5XIjnLnwPbmP/AEa4kV8/x4IrNm8GahH9wxSfRsV2tFNVpITgjz2bQNSg+/aSH/dGf5VUkhkiOHjZT6EYr02mSQxyrtkjVgexGa0WIfVE+z7HmVFd3d+FdNuskQ+Ux7ocfp0rE1HwZcW6l7STzlH8JGDWka0WS4NHP0U6SJ4XKSIUYcEEYIptakBRRRTAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAClh/wBdH/vUlLD/AK6P/eoASiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAoAzwBU1pZzX06wwIWc/pXY6J4Xg04LLMBLP644X6VnOahuVGLexj6P4Rlu1Et2TDGeij7x/wAK6ix0y106PZbwqnqe5/GrNFckqjlubRikFFFFQUFFFFABRRRQAUUUUAFFFFABRRRQBT1DR7TU02zxAnsw4Irj9Z8O3GlMXAMkB6OB0+td5SOiyKUdQVIwQRVwqOBMopnmFFdNrvhLYGuLBcjq0X+Fc0QVOCMEdq7ITUloYuLQlFFFWSFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFLD/ro/wDepKWH/XR/71ACUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVZ0/Tp9SuBDAmT3PZRUdpaS3twkEK5djge1d/pGlRaVaLDGAW6s2OSayqVORFRjcbpGjQaRBsjGXP3nI5NXqKK4223dm6VtgooopDCiiigAooooAKKKKACiiigAooooAKKKKACiiigArE17wzFqCtPbgRzjn2atuinFuLuhNJ6HmMkTwyNG6lWU4IPam12fiXw8L+M3NuoE6jkD+Mf41xpBU4IwRXbTmpowlHlEooorQkKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigApYf9dH/vUlLD/ro/96gBKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAoorR8O2C6hqkcbjKL8zD6Um7K40jpfCujrY2YuJF/fSjP0HYVtUAADAHSiuCUuZ3OhKysFFFFSMKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAK4nxZpf2K+8+NcRTc/Q967aqOt6euo6dLER8wG5PYirpy5ZXJkro89ooIxx6UV3HOFFFFMAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKWH/XR/wC9SUsP+uj/AN6gBKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigArpvA1tl7i4I6AIP6/wBK5mu48I23kaOjEYMjFv6f0rKs7QLgtTYoooriNwooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKMcYoooA881y0+xapPEBgbty/Q81SrpfHFptmhulHDDYf6VzVd1N3ijnkrMKKKK0JCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigApYf9dH/vUlLD/ro/8AeoASiiigAooooAKK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GFJ1dFjY8+uKyK7zwr/yBYT9f51lVk4xui4JNmZr7Ppk2mWds5Rd2DjvXTjoK5XxCTceJ7GAD7nNdVXI9kbIKzPFFz9m0abBwX+Qfj/9atOuY8cXi7YbRTznew9PT+tEFeSQpOyOWooor0DnCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAClh/10f+9SUsP+uj/wB6gBKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACu48I3Pn6Oik5MbFP8/nXD10/gW4w1xbk9QGA/wA/hWVZXgXB2Z1NZN+Cuu2bD+6RWtWfqUf+m2rjqDiuJG5j+Ov+Xb8a5euo8dDi249a5euyj8BhPcKKKK2ICiiigAooooAKKKs6ZYSajdpBGpOT8x9BS2GdJ4L00xQteOMF/lX6V0dMt4EtoUiQYVBgU+uCcuZ3OiKsrGb4nmSDQbot0KbR9TXOX+iu+iWV7CmcQL5gH061o+OpybW3s0zumk6DvW7b2qR2MdsygqsYQg/SqjJxV0Jq+h5tRWlr+ktpd4VUfun5Q+ntWbXZFqSujBq2gUUUVQgooooAKKKKACu88K/8gSH8f51wddppdx/Z/hYTONu1CRmsK/wmlPcpW2NU8ZPKoyluuM+9dTXO+CLVhay3rj5p2z+FdFXNLexqgrg/FRJ1ycZ6bf5Cu5mmS3iaWRgqqMkmvPdXvRqOoy3KrtVjwPYDFa0F71yKmxUooorrMQooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigApYf9dH/vUlLD/ro/8AeoASiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAq/oN9/Z+pxSk4Qna30NUKKTV1Yex6gCCMiqOqOsLQSN0D4pdCuDdaTbyE5OzB+o4qr4tJTR2deqsDXBazsdG6J9e09dT010UfOo3JXAMpUlSMEcV6JpFwLvToZB3UZqjrXheDUf3kG2KUeg4b61pSqcmjIlG+qOIoq5f6Pd6c2Jojt7MOlU66009jK1gooopiCiipLe2lupVihQux4AFLYBscbSuERSzE4AFdx4c0VdMtQ8iDz3HzH09qi8PeHF01BPcANOfyWtuuWrVvojaELasKKKKwNDnL+A33jK2ifHlwReYBXR1zt+XtfGVpLjCTRlM10VU9kJFe/0+HUbdoZlyD0PcVwmraRNpNwY5BlD91uxFd893DFcJbu4V3GUB7029sYNQh8qeMMv8qqnUcPQUo3PNqK39W8IT2xMlnmWP+6eorFls7iBtskLqfQrXXGcZbGLi0RUU7ypB/yzb8qPKf8AuN+VUIbRVy00a9vceTbsQe5GBW1ZeCGyGupgB/dSplOMdxqLZjaRpUuq3IjjGFHLN2Ard8Xv5Nla6XB1kYLgelb1lp9tpsWyCMIMcn1rm7Njrni1piMxW3C1zSnzvyRoo8qOl0+1WysooFHCKBU9FFYmhzPje8kRYbVGwrAsw9fSuVrY8X3In1dkHSJQv9f61j120laCOeerCiiitSQooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigApYf8AXR/71JSw/wCuj/3qAEooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAOz8FT+ZpjRE/6tzj6Vf163+06TcIBk7cgVzvgq9EN5JbMeJVyv1Fde6CSNkPQjFcVVWmbw1ic54GvfMtHtmPMZ4FdJXCaZcnQ9feNwVQvtI9q7pGDqGU8EZFTNWdxx7CSRpKhR1DKexFZz+GNMdixthk+hrToqU2th2TMSXwZp8jllMiA/wg8Cmf8ITY/wDPSX863qKr2ku4uVGD/wAITY/89JfzrT07SbXTI9kEYBPVj1NW6hvbyKwtnuJmCogyaTnJ6NjUUiLUdVt9LRDMx3SNtRFGSx9qtg5AOKwNEs5dWu/7ZvV9reMj7i+tb9J6AgooopDKOs6Umq2ZjyUkX5o3HVTVDwprM12ktlet/pNudpJ6sK3a53WvC015qQu7KYQlxiU5I/EVUbbMT7o1NVsItVtj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Q/xzkitqq9haCzhZB3ctXAdJT8Uu8eizGNivQHHpmuDr0DxGm/Rbkei5/I15/XTh9mY1NwoooroMwooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAClh/10f8AvUlLD/ro/wDeoASiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiige1AHXeBkxZzt6uB+lXvE9j9u0mRQPmT51/Cq/gyF4tKYuhXdISM+mBW2yhlKkcEYrhqv39DeC90zvOZvDpkUlW8jt24rgevNd34hQWugSxxEqFAAx6VwlbYdaNk1B8ErQTJKpwUYEV6RaTrc20cy9HUGvNK3vD3iVNMt2guA7LnKY7VVaHMtCYOzOyrn/G0iDT403DcXyBWimtW9xYPdW7Bii7tvcVw1/fzajcNNM2Seg7AVjSpty9DSUkkV6KKK7DAKKKKAHRyNE4dGKspyCO1d/oOpDUtPSQn51G1/rXn1WbbUbizheKGQoH6kdazqQ50VGXKzp/FGt26WklnFJulbg7e3sa4+lJJOTSUQgoKwSlcKt6Pd/YdQimxnBxVSgcHiraurCPT43EiK69CMilqrpBzpsBz/AKtV57VnY6FsVNYUNpV0D/AM8mP6V51Xpd5B9ptJYQcb0K/mK42bwneRW7yghigztHcVtQko3uRNN7GNRRRXWYhRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFLD/ro/8AepKWH/XR/wC9QAlFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAJGjSMsafMzNtWu6sPDVlaRoHjDuByx7muW8M25utZtweinzP8AvmvQDxXLXm0+VGsIiIixqFUAAdAKWiiuc1MvxT/yBJvw/nXB13viaNpNGnCqSQAeK4KurD7MxqbhRRRXQZkkNxLb7vLcruGDjuKjoopAFFFFMAooooAKKKKACiiigAooq3pFp9t1KCDAwWBP0HWlsM7vSUMenQKwIIQZBq1QAFAA6CivPbu7nQlYKKKKQzgPEmn/AGDUnVV2xyfvI6zq63xtaebZxXS9Ym2t/umuSrtpS5onPL4gooorUkKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAClh/10f8AvUlLD/ro/wDeoASiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAN7wRGDfTyH+GPb+bV2Pauc8D2ojsp5/wC/JtH4V0Zrhqy946I/CLRRRWZQ2SNZYyjAFWGCDXnmrWX9n6hLADkKePpXotYniLw4NRBuIDicDp2ataU+V6kTjdHFUU+aCS2kMcqMjDqCMUyuwwCiiimAUUUUAFFFFABRRRQAUUUAZ4ApAFdR4S0OSOQX067Rj92D1+tR+GvDRmK3d2mEHKIe/ua6wAKAAMAVz1av2Uawh1YUUUVzGoUUUUAUtWtBe6dNbjG5kO369q88r08157rtl9i1WeP+HduX/dat8PL7JnUKVFa91HC0U0YgiXy4lZWVcHJpjwwxwxDyk3RFS52/ez610cxnYy6K1/ssUMm1o0O+5CjK9uuKIFijUg28Lb7sx/MnQe1HMHKZFFW4o4I9UMcoAiWQjnp7VYmhSW6t0a2WJskttA2MO2KfMKxmUVqtDDLK5SJAJIdygKPlI9KVYYYZlLxIViiBYFRyTS5x2MmitSKBIDcsIElKuqqpXIwfQU+C3XN4ILdJCrLsWROg79elHOFjIoooqyQooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAClh/wBdH/vUlLD/AK6P/eoASiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooq/YeHb+/2ssPlp/ff5amUuUChUtnYz38qw28bEt/47XV2Hg2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fwe1Yuo+FLK9y0a/Z5cfejHH/fNdMcR/MZypnFUVd1PQrvS/mlj3R/31+7VKuiMuYzCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKWH/XR/71JSw/66P/eoASiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKW3gkuZFjhjaR2/hVa1dG8Lz6ltlm3Qw+v8AE3+7XX2Om22mR+XbxKvqe5rGpWjE0jT5jntM8Fs217+Tb/0zj/8Aiq6S0sbexj8u3hWNf9mrGBRgVySqSkaRjyhRRRSKCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigBhQMNrDIrA1Xwhb3O6S0Cwy/wB3+E/4V0NGR6VUZSh8IpRueaXdpPZSmGeNo396jr0a90+31KHybiNWX+H1WuN1rw5PpLeYm6aD+/8A3f8Aerpp1+YylHlMuiiitzMKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKWH/XR/71JSw/66P/AHqAEooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAoopPvf71AAqtIyqqszN91VrrNB8KLahbi9VWl+8qdl+tT+HPDq6ei3Nwubhug/uVu9BXLUrfZibRiLRRRXOaBRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAU1kV1KsuVNOooA4/xD4X+zFruyXMX3nj9P/sa5+vT8VyPifw35G69s1wn/LSNf4f9quqjW+zIxlE56iiiugzCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKWH/XR/wC9SUsP+uj/AN6gBKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAK6Twho3mN9vnX5V/wBSv/s1YOnWLalexWq/xt8zf3Vr0WCCO2hWKJQqIu1VrCtU5fdNKcSWiiiuQ2CiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACjgiiigDhfE2if2bP5sK/6PI3y/wCy392smvR7y0jv7Z7eZdyOuDXnt7aSafcvby9Ub/vquuhU5tDGpHlIqKKK3MwooooAKKKKACiiigAooooAKKKKACiiigAooooAKWH/AF0f+9SUsP8Aro/96gA8qT/nm3/fNHlSf882/wC+a0aKVx2M7ypP+ebf980eVJ/zzb/vmtGii4WM7ypP+ebf980eVJ/zzb/vmtGii4WM7ypP+ebf980eVJ/zzb/vmtGii4WM7ypP+ebf980eVJ/zzb/vmtGii4WM7ypP+ebf980eVJ/zzb/vmtGii4WM7ypP+ebf980eVJ/zzb/vmtGii4WM7ypP+ebf980eVJ/zzb/vmtGii4WM7ypP+ebf980eVJ/zzb/vmtGii4WNXwVp+0TXcibW/wBWuf8Ax6uoHSsfw1/yDn/67PWwOlcdV3kbx+EWiiisigooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAD0rnPGGlfabVbuNf3kXDf7S10Z6Vn69/yCZ/92qpu0tCZHAeVJ/zzb/vmjypP+ebf981o0V33MLGd5Un/ADzb/vmjypP+ebf981o0UXCxneVJ/wA82/75o8qT/nm3/fNaNFFwsZ3lSf8APNv++aPKk/55t/3zWjRRcLGd5Un/ADzb/vmjypP+ebf981o0UXCxneVJ/wA82/75o8qT/nm3/fNaNFFwsZ3lSf8APNv++aPKk/55t/3zWjRRcLGd5Un/ADzb/vmjypP+ebf981o0UXCxneVJ/wA82/75p0EUnmR/u2+9/dq/Sw/6xP8AeouFj//Z";
		byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(example.getBytes());

		user.addUser(
				sessionFactory,
				"john@smith.es",
				"12345",
				"John",
				"Smith",
				"IBM",
				"659565945",
				decoded);

		auth_user auth_userRecord = auth_user.getUser(sessionFactory, "john@smith.es");
		Assertions.assertNotNull(auth_userRecord);
		Assertions.assertTrue(auth_userRecord.hasRole("regularuser"));
	}

	@Test
	void deleteUser() {
		Integer user_id = user.addUser(sessionFactory, "deluser@test.es", "12345", "John", "Smith", null, null, null)
				.getUserId();

		assertNotNull(model.user.getUser(sessionFactory, user_id));

		user.deleteUser(sessionFactory, user_id);

		assertNull(model.user.getUser(sessionFactory, user_id));

	}

	@Test
	void getUser() {
		user adduser = user.addUser(sessionFactory, "getuser@test.es", "12345", "John", "Smith", null, null, null);
		user getuser = model.user.getUser(sessionFactory, adduser.getUserId());

		assertNotNull(getuser);
		assertEquals(adduser.getUserId(), getuser.getUserId());
	}

	@Test
	void listUsers() {
		user.getUsersList(sessionFactory);
	}

	@Test
	void searchUsers() {
		user.addUser(sessionFactory, "searchuser1@test.es", "12345", "Mary", "Walker", "Know Industry", null, null);
		user.addUser(sessionFactory, "searchuser2@test.es", "12345", "Tony", "Darma", "Bluestone Industry", null, null);
		user.addUser(sessionFactory, "searchuser3@test.es", "12345", "Elliese", "Indy", "Dharma Indstry", null, null);
		user.addUser(sessionFactory, "searchuser4@test.es", "12345", "Paul", "Berry", "Challenger", null, null);

		List<user> listUsers = user.searchUsers(sessionFactory, "Indstry");
		assertEquals(3, listUsers.size());
		assertEquals("Know Industry", listUsers.get(0).getUserCompany());
		assertEquals("Bluestone Industry", listUsers.get(1).getUserCompany());
		assertEquals("Dharma Indstry", listUsers.get(2).getUserCompany());

	}

	@Test
	void updateUser() {
		user upduser = user.addUser(sessionFactory, "upduser@test.es", "12345", "John", "Smith", null, null, null);
		user.updateUser(sessionFactory, upduser.getUserId(), "Mark", "Brown", "IBM", "12345678", null);


		Session session = sessionFactory.openSession();
		try {
			session.refresh(upduser);
			assertEquals("Mark", upduser.getUserFirstname());
			assertEquals("Brown", upduser.getUserLastname());
			assertEquals("IBM", upduser.getUserCompany());
			assertEquals("12345678", upduser.getUserPhone());
		} finally {
			session.close();
		}
	}
}