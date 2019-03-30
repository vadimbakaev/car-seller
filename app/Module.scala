import java.time.Clock

import com.google.inject.AbstractModule
import com.google.inject.name.Names

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Clock])
      .annotatedWith(Names.named("clock"))
      .toInstance(Clock.systemUTC())
  }

}
