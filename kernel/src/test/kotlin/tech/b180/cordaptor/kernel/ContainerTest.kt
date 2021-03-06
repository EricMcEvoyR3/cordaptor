package tech.b180.cordaptor.kernel

import com.typesafe.config.ConfigFactory
import org.junit.BeforeClass
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import java.time.Duration
import kotlin.test.*

class ContainerTest {

  companion object {

    private lateinit var containerInstance: Container

    @BeforeClass @JvmStatic
    fun `init`() {
      val config = TypesafeConfig.loadDefault()
      containerInstance = Container(config) {
        module(override = true) {
          single { WrapperComponent(get()) }
          single<TestService>(named("override")) {
            OuterTestServiceImpl(get(named("override")))
          } bind Marker::class

          // reexporting an existing definition to emulate disabled conditional override
          single<TestService>(named("no-override")) {
            get(InnerTestService::class, named("no-override"))
          } bind Marker::class
        }
      }
    }
  }

  @Test
  fun `test module provider locator`() {
    assertNotNull(containerInstance, "Container must have been created")

    val component = containerInstance.get(TestComponent::class)
    assertNotNull(component)

    val wrapper = containerInstance.get(WrapperComponent::class)
    assertNotNull(wrapper)
    assertSame(wrapper.testComponent, component)
  }

  @Test
  fun `test property access`() {
    val component = containerInstance.get(TestComponent::class)

    // values are defined in /koin.properties
    assertEquals("ABC", component.stringValue)
    assertEquals(true, component.booleanValue)
    assertEquals(123, component.integerValue)
    assertEquals(1.5, component.doubleValue)
    assertEquals(Duration.ofSeconds(2), component.durationValue)
    assertEquals(10 * 1024 * 1024, component.byteSizeValue)

    assertEquals(listOf("A", "B", "C"), component.listValue)
    assertEquals(emptyList(), component.emptyListValue)
  }

  @Test
  fun `test implementation overrides`() {
    val service1 = containerInstance.get(TestService::class, named("override"))
    assertEquals("Outer work(Inner work)", service1.doWork())

    val service2 = containerInstance.get(TestService::class, named("no-override"))
    assertTrue(service2 is InnerTestService)
    assertEquals("Inner work", service2.doWork())
  }

  @Test
  fun `test markers`() {
    val markers = containerInstance.getAll(Marker::class)
    assertEquals(4, markers.size)

    // unfortunate side effect of Koin library
    // the test makes sure that if the behaviour changes, it gets captured
    val nonOverridenService = containerInstance.get(TestService::class, named("no-override"))
    assertEquals(2, markers.count { it == nonOverridenService }, "Reexported object is bound twice")
  }

  @Test
  fun `test secrets`() {
    val config = containerInstance.get(Config::class)
    val secret = config.getStringSecret("secrets.string")

    assertEquals("secrets.string", secret.id)
    assertEquals("secrets.string", config.getSubtree("secrets").getStringSecret("string").id)

    val secretsStore = containerInstance.get(SecretsStore::class)

    val secretValue = secret.use(secretsStore) { String(it) }
    assertEquals("Secret Value", secretValue)
  }
}

interface Marker

class TestComponent(
    val stringValue: String,
    val booleanValue: Boolean,
    val integerValue: Int,
    val durationValue: Duration,
    val doubleValue: Double,
    val byteSizeValue: Long,
    val listValue: List<String>,
    val emptyListValue: List<String>
) : CordaptorComponent

class WrapperComponent(val testComponent: TestComponent) : CordaptorComponent

interface TestService : Marker {
  fun doWork(): String
}

interface InnerTestService : TestService

class InnerTestServiceImpl : InnerTestService {
  override fun doWork() = "Inner work"
}

class OuterTestServiceImpl(private val delegate: InnerTestService) : TestService {
  override fun doWork() = "Outer work(${delegate.doWork()})"
}

/**
 * This class is instantiated by [Container] using [java.util.ServiceLoader]
 * because it is declared in META-INF/services
 */
@Suppress("UNUSED")
class ContainerTestModuleProvider : ModuleProvider {
  override fun provideModule(moduleConfig: Config): Module = module {
    single { TestComponent(
        moduleConfig.getString("stringValue"),
        moduleConfig.getBoolean("booleanValue"),
        moduleConfig.getInt("integerValue"),
        moduleConfig.getDuration("durationValue"),
        moduleConfig.getDouble("doubleValue"),
        moduleConfig.getBytesSize("bytesSizeValue"),
        moduleConfig.getStringsList("listValue"),
        moduleConfig.getStringsList("emptyListValue")
    ) }
    single<InnerTestService>(named("override")) { InnerTestServiceImpl() } bind TestService::class bind Marker::class
    single<InnerTestService>(named("no-override")) { InnerTestServiceImpl() } bind TestService::class bind Marker::class
  }

  override val salience = ModuleProvider.INNER_MODULE_SALIENCE
  override val configPath = "testModule"
}

/**
 * This class is instantiated by [Container] using [java.util.ServiceLoader]
 * because it is declared in META-INF/services, but the kernel will never initialize it
 * because it is disabled in the config.
 */
class DisabledTestModuleProvider : ModuleProvider {
  override fun provideModule(moduleConfig: Config): Module {
    throw AssertionError("Not called when the module is disabled in config")
  }

  override val salience: Int = 0
  override val configPath: ConfigPath = "disabledModule"
}