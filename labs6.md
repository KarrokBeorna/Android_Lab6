# Цели

Получить практические навыки разработки многопоточных приложений:

1. Организация обработки длительных операций в background (worker) thread:
    - Запуск фоновой операции (coroutine/asynctask/thread)
    - Остановка фоновой операции (coroutine/asynctask/thread)
2. Публикация данных из background (worker) thread в main (ui) thread.

Освоить 3 основные группы API для разработки многопоточных приложений:

1. Kotlin Coroutines
2. AsyncTask
3. Java Threads

## Задача 1 - Альтернативные решения задачи "не секундомер" из Лаб. 2

Собственно, как и указано в задании, мы используем практически тот же код, однако теперь мы следим, где запускается и останавливается поток.

__Листинг 1.1 - Task1_Threads.kt__

    package com.example.android_lab6

    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.util.Log
    import kotlinx.android.synthetic.main.continuewatch.*

    private const val SECONDS_EL = "seconds_el"
    //const val SLEEP = "sleep"

    class Task1_Threads : AppCompatActivity() {
        private var secondsElapsed = 0
        //var sleep = 1000
        private var backgroundThread: Thread? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (savedInstanceState != null) {
                secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
                //sleep = savedInstanceState.getInt(SLEEP)
            }
            setContentView(R.layout.continuewatch)
        }

        override fun onPause() {
            backgroundThread?.interrupt()
            super.onPause()
        }

        override fun onResume() {
            backgroundThread = Thread {
                try {
                    while (backgroundThread?.isInterrupted == false) {
                        /**while(sleep != 0) {
                            Thread.sleep(1)
                            sleep--
                        }
                        sleep = 1000 */
                        Thread.sleep(1000)
                        textSecondsElapsed.post {
                            textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
                        }
                        Log.i("Thread", "Time = $secondsElapsed")
                    }
                } catch (e: InterruptedException) {
                    /** Возможно, как-то здесь стоит брать время прошлого post()
                     *  после этого смотреть на время сообщения "Thread paused",
                     *  вычитать эту разницу из 1000 и сохранять полученное значение
                     *  в глобальную переменную SLEEP
                     */
                    Log.i("Thread", "Thread paused")
                }
            }

            backgroundThread?.start()
            super.onResume()
        }

        override fun onSaveInstanceState(outState: Bundle) {
            outState.putInt(SECONDS_EL, secondsElapsed)
            //outState.putInt(SLEEP, sleep)
            super.onSaveInstanceState(outState)
        }

        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
            //sleep = savedInstanceState.getInt(SLEEP)
            super.onRestoreInstanceState(savedInstanceState)
        }
    }

- Как и во второй работе используем глобальную переменную для запоминания времени.
- Добавляем переменную для потока, к которой будем обращаться в методах `onPause()` и `onResume()`.
- Метод `onCreate()` – будем брать сохраненное число из глобальной переменной, если уже был хоть раз использован метод `onPause()`. Естественно, отображаем всё на экран.
- Метод `onPause()` – здесь мы будем закрывать поток, когда активити переходит в состояние паузы.
- Метод `onResume()` – если не было вызова о закрытии потока, то спим 1000 мс и обновляем текстовое поле. В Logcat выводим текущее значение секундомера. Если был вызван метод `interrupt()`, то в Logcat выводим сообщение о том, что поток остановлен.
- Методы `onSaveInstanceState()` и `onRestoreInstanceState()` – оставили такими же, как и во 2 работе.

Также я попытался сделать так, чтобы мы сохраняли текущее значение sleep в глобальную переменную и при восстановлении приложения мы не ждали опять секунду, а прождали лишь оставшееся время, но я это так и не смог реализовать. Оставил свои предположения в комментарии.

Собственно, вот наглядный пример того, как мы ждем не 1 секунду, а целых 2:

    2021-01-10 20:26:04.284 16616-16616/com.example.android_lab6 I/Thread: Time = 12
    2021-01-10 20:26:05.237 16616-16616/com.example.android_lab6 I/Thread: Thread paused
    2021-01-10 20:26:06.375 16616-16616/com.example.android_lab6 I/Thread: Time = 13

Теперь выполним данную задачу при помощи AsyncTask.

__Листинг 1.2 - Task1_Async.kt__

    package com.example.android_lab6

    import android.annotation.SuppressLint
    import android.os.AsyncTask
    import android.os.Bundle
    import android.util.Log
    import androidx.appcompat.app.AppCompatActivity
    import kotlinx.android.synthetic.main.continuewatch.*

    private const val SECONDS_EL = "seconds_el"

    class Task1_Async : AppCompatActivity() {
        var secondsElapsed = 0
        private var backgroundTask: NoTimer? = null

        @SuppressLint("StaticFieldLeak")
        inner class NoTimer : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                while (!isCancelled) {
                    Thread.sleep(1000)
                    publishProgress()
                }
            }

            override fun onProgressUpdate(vararg values: Unit?) {
                super.onProgressUpdate(*values)
                textSecondsElapsed.post {
                    textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
                }
                Log.i("Task", "Time = $secondsElapsed")
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (savedInstanceState != null) {
                secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
            }
            setContentView(R.layout.continuewatch)
        }

        override fun onPause() {
            backgroundTask?.cancel(false)
            Log.i("Task", "Task paused")
            super.onPause()
        }

        override fun onResume() {
            backgroundTask = NoTimer()
            backgroundTask?.execute()
            super.onResume()
        }

        override fun onSaveInstanceState(outState: Bundle) {
            outState.putInt(SECONDS_EL, secondsElapsed)
            super.onSaveInstanceState(outState)
        }

        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
            super.onRestoreInstanceState(savedInstanceState)
        }
    }

- Главное отличие - создание собственного класса `AsyncTask`. У нас он является `inner`, чтобы использовать переменные внешнего класса, в нашем случае `secondsElapsed`.
- Аннотация `@SuppressLint("StaticFieldLeak")` для подавления предупреждения о том, что нестатический внутренний класс AsyncTask может вызвать утечку контекста.
- Метод `doInBackground()` – основной метод, в котором находится код для тяжелых задач. В нашем случае – спим 1000 мс и отправляем данные обработчику с помощью метода `publishProgress()`. Проверка `isCancelled` нужна для того, чтобы завершить задачу, когда будет вызван метод `cancel()`.
- Метод `onProgressUpdate()` – получает на вход промежуточные данные от `publishProgress()` (в нашем случае ничего) и обновляем текстовое поле.
- Метод `onCreate()` – не изменяем.
- Метод `onPause()` – вместо метода `interrupt()` используем `cancel()` для отмены задачи.
- Метод `onResume()` – как и в случае с потоками, здесь мы создаем объект класса AsyncTask, после чего начинаем выполнение задачи с помощью метода `execute()`.
- Методы `onSaveInstanceState()` и `onRestoreInstanceState()` – не изменяем.

Наконец, выполним задачу еще раз, но уже с помощью корутин.

Для начала пропишем зависимости:

    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1'
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0"

__Листинг 1.3 - Task1_Coroutines__

    package com.example.android_lab6

    import android.os.Bundle
    import android.util.Log
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.lifecycleScope
    import kotlinx.android.synthetic.main.continuewatch.*
    import kotlinx.coroutines.*

    private const val SECONDS_EL = "seconds_el"

    class Task1_Coroutines : AppCompatActivity() {
        var secondsElapsed = 0
        private var job: Job? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (savedInstanceState != null) {
                secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
            }
            setContentView(R.layout.continuewatch)
        }

        override fun onPause() {
            job?.cancel()
            Log.i("Coroutines", "Coroutines paused")
            super.onPause()
        }

        override fun onResume() {
            job = lifecycleScope.launchWhenResumed {
                while (isActive) {
                    delay(1000)
                    textSecondsElapsed.post {
                        textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
                    }
                    Log.i("Coroutines", "Time = $secondsElapsed")
                }
            }
            super.onResume()
        }

        override fun onSaveInstanceState(outState: Bundle) {
            outState.putInt(SECONDS_EL, secondsElapsed)
            super.onSaveInstanceState(outState)
        }

        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            secondsElapsed = savedInstanceState.getInt(SECONDS_EL)
            super.onRestoreInstanceState(savedInstanceState)
        }
    }

Если взглянуть на код, то заметим, что отличий от потоков очень мало:
1. Вместо `Thread` мы используем `Job`. Это как раз корутиновский интерфейс для работы с ними.
2. В методе `onPause()`, почти как и при AsyncTask, мы вызываем метод `cancel()` для нашего Job, который отменяет работу.
3. В методе `onResume()`, как и в случае с потоком, мы запускаем нашу корутину, в которой используется функция `delay`, заменяющая `Thread.sleep`. Для запуска корутины используется метод `launchWhenResumed()` из сторонней библиотеки `lifecycle`.
4. Вместо `isInterrupted` мы используем `isActive`.

## Задача 2 - Загрузка картинки в фоновом потоке (AsyncTask)

Самое важное - добавляем в Манифест 1 строчку:

    <uses-permission android:name="android.permission.INTERNET" />

Она отвечает за загрузку данных из Интернета.

Берем готовый код по AsyncTask [отсюда](https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android/9288544#9288544).
Конвертируем его под Kotlin и немного изменим:

__Листинг 2.1 - DownloadImageTask__

    inner class DownloadImageTask : AsyncTask<String?, Void?, Bitmap?>() {
        override fun doInBackground(vararg urls: String?): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val stream: InputStream = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(stream)
            } catch (e: Exception) { }
            return mIcon11
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            image.setImageBitmap(result)
            button.visibility = INVISIBLE
        }
    }

- В методе `doInBackground()` мы загружаем нашу картинку.
- В методе `onPostExecute()` мы кладем нашу картинку в `imageView` и скрываем кнопку.
- Метод `onCreate()`, в котором идет отображение нашего layout, а также обработка нажатия на кнопку.

__Листинг 2.2 - onCreate()__

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task2_download)

        button.setOnClickListener {
            DownloadImageTask().execute(imageURL)
        }
    }

## Задача 3 - Загрузка картинки в фоновом потоке (Kotlin Coroutines)

Попробуем теперь загрузить картинку при помощи корутин. Здесь у нас будет лишь метод `onCreate()`.

__Листинг 3.1 - Task2_Coroutines.kt__

    package com.example.android_lab6

    import android.graphics.Bitmap
    import android.graphics.BitmapFactory
    import android.os.Bundle
    import android.view.View
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.lifecycleScope
    import kotlinx.android.synthetic.main.task2_download.*
    import kotlinx.coroutines.*
    import java.io.InputStream
    import java.net.URL

    @Suppress("BlockingMethodInNonBlockingContext")
    class Task2_Coroutines : AppCompatActivity() {

        private val imageURL = "https://vsezhivoe.ru/wp-content/uploads/2017/09/%D0%A4%D0%BE%D1%82%D0%BE1-%D0%9B%D0%B8%D1%81%D0%B8%D1%86%D0%B0-%D0%BE%D0%B1%D1%8B%D0%BA%D0%BD%D0%BE%D0%B2%D0%B5%D0%BD%D0%BD%D0%B0%D1%8F.jpg"

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.task2_download)

            button.setOnClickListener {
                lifecycleScope.launchWhenResumed {
                    var mIcon11: Bitmap? = null
                    withContext(Dispatchers.IO) {
                        try {
                            val stream: InputStream = URL(imageURL).openStream()
                            mIcon11 = BitmapFactory.decodeStream(stream)
                        } catch (e: Exception) { }
                    }
                    withContext(Dispatchers.Main) {
                        image.setImageBitmap(mIcon11)
                        button.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

В обработчике нажатия мы запускаем нашу корутину, а дальше используем методы `withContext()`, которые блокируют выполнение, пока не закончат работу сами. Таким образом, мы уверены, что картинка успеет загрузиться до того момента, когда её нужно отображать на экране.

Предварительно я много времени потратил на попытку загрузки без `withContext()`, но всегда картинка не успевала загрузиться. К счастью, Андрей Николаевич в лекциях объяснил как использовать `withContext()`.
`Dispatchers.IO` – диспетчер, оптимизированный для работы с памятью и сетью.
`Dispatchers.Main` – диспетчер для работы с главным потоком.

## Задача 4 - Использование сторонних библиотек (Picasso)

Добавляем одну зависимость для `Picasso`:

    implementation 'com.squareup.picasso:picasso:2.71828'

__Листинг 4.1 - Task2_Picasso__

    package com.example.android_lab6

    import android.os.Bundle
    import android.view.View.INVISIBLE
    import androidx.appcompat.app.AppCompatActivity
    import com.squareup.picasso.Picasso
    import kotlinx.android.synthetic.main.task2_download.*

    class Task2_Picasso : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.task2_download)

            button.setOnClickListener {
                Picasso.get().load("https://vsezhivoe.ru/wp-content/uploads/2017/09/%D0%A4%D0%BE%D1%82%D0%BE1-%D0%9B%D0%B8%D1%81%D0%B8%D1%86%D0%B0-%D0%BE%D0%B1%D1%8B%D0%BA%D0%BD%D0%BE%D0%B2%D0%B5%D0%BD%D0%BD%D0%B0%D1%8F.jpg")
                    .into(image)
                button.visibility = INVISIBLE
            }
        }
    }

Получили такой замечательный короткий код в 2 строчки.
Метод `load()` загружает картинку, метод `into()` кладет картинку в наш блок `imageView`. После этого скрываем кнопку.

# Выводы

Если подвести итог по первой задаче, то принципиальных отличий на таком простом действии как секундомер я не заметил. Всё выполняется примерно с одинаковой задержкой. Разницы в написании кода я тоже не заметил, благо все заготовки нам преподнесли на блюдечке на лекциях.
Вторая задача, конечно, немного подставила с `withContext()`, но проблема решилась просмотром одной лекции.

В целом, нас пытались лишь чуть-чуть познакомить с корутинами, поэтому я совсем не почувствовал разницы между ними и потоками, поэтому всё равно предпочел бы использовать то, что удобнее и легче в понимании.

Общее время выполнения работы с просмотром на фоне 2,5 лекций - 11 часов.
