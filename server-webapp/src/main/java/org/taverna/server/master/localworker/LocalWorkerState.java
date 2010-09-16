package org.taverna.server.master.localworker;

import static java.io.File.separator;
import static java.lang.System.getProperty;
import static java.rmi.registry.Registry.REGISTRY_PORT;
import static org.taverna.server.master.localworker.LocalWorkerManagementState.KEY;
import static org.taverna.server.master.localworker.LocalWorkerManagementState.makeInstance;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.PersistenceAware;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.springframework.beans.factory.annotation.Required;
import org.taverna.server.master.common.Status;
import org.taverna.server.master.localworker.PersistentContext.Action;

/**
 * The persistent state of a local worker factory.
 * 
 * @author Donal Fellows
 */
@PersistenceAware
public class LocalWorkerState {
	/**
	 * The name of the resource that is the implementation of the subprocess
	 * that this class will fork off.
	 */
	public static final String SUBPROCESS_IMPLEMENTATION_JAR = "util/server.worker.jar";

	/**
	 * @param persistenceManagerFactory
	 *            The JDO engine to use for managing persistence of the state.
	 */
	@Required
	public void setPersistenceManagerFactory(
			PersistenceManagerFactory persistenceManagerFactory) {
		ctx = new PersistentContext<LocalWorkerManagementState>(
				persistenceManagerFactory);
	}

	PersistentContext<LocalWorkerManagementState> ctx;

	/** Initial lifetime of runs, in minutes. */
	int defaultLifetime;
	private static final int DEFAULT_DEFAULT_LIFE = 20;
	/**
	 * Maximum number of runs to exist at once. Note that this includes when
	 * they are just existing for the purposes of file transfer (
	 * {@link Status#Initialized}/{@link Status#Finished} states).
	 */
	int maxRuns;
	private static final int DEFAULT_MAX = 5;
	/**
	 * Prefix to use for RMI names.
	 */
	String factoryProcessNamePrefix;
	private static final String DEFAULT_PREFIX = "ForkRunFactory.";
	/**
	 * Full path name of the script used to start running a workflow; normally
	 * expected to be "<i>somewhere/</i><tt>executeWorkflow.sh</tt>".
	 */
	String executeWorkflowScript;
	transient String defaultExecuteWorkflowScript;
	/**
	 * The extra arguments to pass to the subprocess.
	 */
	String[] extraArgs;
	private static final String[] DEFAULT_EXTRA_ARGS = new String[0];
	/**
	 * How long to wait for subprocess startup, in seconds.
	 */
	int waitSeconds;
	private static final int DEFAULT_WAIT = 40;
	/**
	 * Polling interval to use during startup, in milliseconds.
	 */
	int sleepMS;
	private static final int DEFAULT_SLEEP = 1000;
	/**
	 * Full path name to the worker process's implementation JAR.
	 */
	String serverWorkerJar;
	private static final String DEFAULT_WORKER_JAR = LocalWorkerState.class
			.getClassLoader().getResource(SUBPROCESS_IMPLEMENTATION_JAR)
			.getFile();
	/**
	 * Full path name to the Java binary to use to run the subprocess.
	 */
	String javaBinary;
	private static final String DEFAULT_JAVA_BINARY = getProperty("java.home")
			+ separator + "bin" + separator + "java";
	String registryHost;
	int registryPort;

	/**
	 * @param defaultLifetime
	 *            how long a workflow run should live by default, in minutes.
	 */
	public void setDefaultLifetime(int defaultLifetime) {
		this.defaultLifetime = defaultLifetime;
		store();
	}

	/**
	 * @return how long a workflow run should live by default, in minutes.
	 */
	public int getDefaultLifetime() {
		load();
		return defaultLifetime < 1 ? DEFAULT_DEFAULT_LIFE : defaultLifetime;
	}

	/**
	 * @param maxRuns
	 *            the maxRuns to set
	 */
	public void setMaxRuns(int maxRuns) {
		this.maxRuns = maxRuns;
		store();
	}

	/**
	 * @return the maxRuns
	 */
	public int getMaxRuns() {
		load();
		return maxRuns < 1 ? DEFAULT_MAX : maxRuns;
	}

	/**
	 * @param factoryProcessNamePrefix
	 *            the factoryProcessNamePrefix to set
	 */
	public void setFactoryProcessNamePrefix(String factoryProcessNamePrefix) {
		this.factoryProcessNamePrefix = factoryProcessNamePrefix;
		store();
	}

	/**
	 * @return the factoryProcessNamePrefix
	 */
	public String getFactoryProcessNamePrefix() {
		load();
		return factoryProcessNamePrefix == null ? DEFAULT_PREFIX
				: factoryProcessNamePrefix;
	}

	/**
	 * @param executeWorkflowScript
	 *            the executeWorkflowScript to set
	 */
	public void setExecuteWorkflowScript(String executeWorkflowScript) {
		this.executeWorkflowScript = executeWorkflowScript;
		store();
	}

	/**
	 * @return the executeWorkflowScript
	 */
	public String getExecuteWorkflowScript() {
		load();
		return executeWorkflowScript == null ? defaultExecuteWorkflowScript
				: executeWorkflowScript;
	}

	/**
	 * @param extraArgs
	 *            the extraArgs to set
	 */
	public void setExtraArgs(String[] extraArgs) {
		this.extraArgs = extraArgs;
		store();
	}

	/**
	 * @return the extraArgs
	 */
	public String[] getExtraArgs() {
		load();
		return extraArgs == null ? DEFAULT_EXTRA_ARGS : extraArgs;
	}

	/**
	 * @param waitSeconds
	 *            the waitSeconds to set
	 */
	public void setWaitSeconds(int waitSeconds) {
		this.waitSeconds = waitSeconds;
		store();
	}

	/**
	 * @return the waitSeconds
	 */
	public int getWaitSeconds() {
		load();
		return waitSeconds < 1 ? DEFAULT_WAIT : waitSeconds;
	}

	/**
	 * @param sleepMS
	 *            the sleepMS to set
	 */
	public void setSleepMS(int sleepMS) {
		this.sleepMS = sleepMS;
		store();
	}

	/**
	 * @return the sleepMS
	 */
	public int getSleepMS() {
		load();
		return sleepMS < 1 ? DEFAULT_SLEEP : sleepMS;
	}

	/**
	 * @param serverWorkerJar
	 *            the serverWorkerJar to set
	 */
	public void setServerWorkerJar(String serverWorkerJar) {
		this.serverWorkerJar = serverWorkerJar;
		store();
	}

	/**
	 * @return the serverWorkerJar
	 */
	public String getServerWorkerJar() {
		load();
		return serverWorkerJar == null ? DEFAULT_WORKER_JAR : serverWorkerJar;
	}

	/**
	 * @param javaBinary
	 *            the javaBinary to set
	 */
	public void setJavaBinary(String javaBinary) {
		this.javaBinary = javaBinary;
		store();
	}

	/**
	 * @return the javaBinary
	 */
	public String getJavaBinary() {
		load();
		return javaBinary == null ? DEFAULT_JAVA_BINARY : javaBinary;
	}

	/**
	 * @param registryHost
	 *            the registryHost to set
	 */
	public void setRegistryHost(String registryHost) {
		this.registryHost = (registryHost == null ? "" : registryHost);
		store();
	}

	/**
	 * @return the registryHost
	 */
	public String getRegistryHost() {
		load();
		return registryHost.isEmpty() ? null : registryHost;
	}

	/**
	 * @param registryPort
	 *            the registryPort to set
	 */
	public void setRegistryPort(int registryPort) {
		this.registryPort = ((registryPort < 1 || registryPort > 65534) ? REGISTRY_PORT
				: registryPort);
		store();
	}

	/**
	 * @return the registryPort
	 */
	public int getRegistryPort() {
		load();
		return registryPort == 0 ? REGISTRY_PORT : registryPort;
	}

	// --------------------------------------------------------------

	private boolean loadedState;

	public void load() {
		if (loadedState || ctx == null)
			return;
		ctx.inTransaction(new Action<RuntimeException>() {
			@Override
			public void act() {
				LocalWorkerManagementState state = ctx.getByID(
						LocalWorkerManagementState.class, KEY);
				if (state == null) {
					return;
				}

				defaultLifetime = state.getDefaultLifetime();
				executeWorkflowScript = state.getExecuteWorkflowScript();
				extraArgs = state.getExtraArgs();
				factoryProcessNamePrefix = state.getFactoryProcessNamePrefix();
				javaBinary = state.getJavaBinary();
				maxRuns = state.getMaxRuns();
				serverWorkerJar = state.getServerWorkerJar();
				sleepMS = state.getSleepMS();
				waitSeconds = state.getWaitSeconds();
				registryHost = state.getRegistryHost();
				registryPort = state.getRegistryPort();
			}
		});
		loadedState = true;
	}

	private void store() {
		if (ctx == null)
			return;
		ctx.inTransaction(new Action<RuntimeException>() {
			@Override
			public void act() {
				LocalWorkerManagementState state = ctx.getByID(
						LocalWorkerManagementState.class, KEY);
				if (state == null) {
					state = ctx.persist(makeInstance());
				}

				state.setDefaultLifetime(defaultLifetime);
				state.setExecuteWorkflowScript(executeWorkflowScript);
				state.setExtraArgs(extraArgs);
				state.setFactoryProcessNamePrefix(factoryProcessNamePrefix);
				state.setJavaBinary(javaBinary);
				state.setMaxRuns(maxRuns);
				state.setServerWorkerJar(serverWorkerJar);
				state.setSleepMS(sleepMS);
				state.setWaitSeconds(waitSeconds);
				state.setRegistryHost(registryHost);
				state.setRegistryPort(registryPort);
			}
		});
		loadedState = true;
	}
}

@javax.jdo.annotations.PersistenceCapable
class LocalWorkerManagementState {
	static LocalWorkerManagementState makeInstance() {
		LocalWorkerManagementState o = new LocalWorkerManagementState();
		o.ID = KEY;
		return o;
	}

	@PrimaryKey(column = "ID")
	protected int ID;

	static final int KEY = 32;

	@Persistent
	private int defaultLifetime;
	@Persistent
	private int maxRuns;
	@Persistent
	private String factoryProcessNamePrefix;
	@Persistent
	private String executeWorkflowScript;
	@Persistent(serialized = "true")
	private String[] extraArgs;
	@Persistent
	private int waitSeconds;
	@Persistent
	private int sleepMS;
	@Persistent
	private String serverWorkerJar;
	@Persistent
	private String javaBinary;
	@Persistent
	private int registryPort;
	@Persistent
	private String registryHost;

	/**
	 * @param defaultLifetime
	 *            how long a workflow run should live by default, in minutes.
	 */
	public void setDefaultLifetime(int defaultLifetime) {
		this.defaultLifetime = defaultLifetime;
	}

	/**
	 * @return how long a workflow run should live by default, in minutes.
	 */
	public int getDefaultLifetime() {
		return defaultLifetime;
	}

	/**
	 * @param maxRuns
	 *            the maxRuns to set
	 */
	public void setMaxRuns(int maxRuns) {
		this.maxRuns = maxRuns;
	}

	/**
	 * @return the maxRuns
	 */
	public int getMaxRuns() {
		return maxRuns;
	}

	/**
	 * @param factoryProcessNamePrefix
	 *            the factoryProcessNamePrefix to set
	 */
	public void setFactoryProcessNamePrefix(String factoryProcessNamePrefix) {
		this.factoryProcessNamePrefix = factoryProcessNamePrefix;
	}

	/**
	 * @return the factoryProcessNamePrefix
	 */
	public String getFactoryProcessNamePrefix() {
		return factoryProcessNamePrefix;
	}

	/**
	 * @param executeWorkflowScript
	 *            the executeWorkflowScript to set
	 */
	public void setExecuteWorkflowScript(String executeWorkflowScript) {
		this.executeWorkflowScript = executeWorkflowScript;
	}

	/**
	 * @return the executeWorkflowScript
	 */
	public String getExecuteWorkflowScript() {
		return executeWorkflowScript;
	}

	/**
	 * @param extraArgs
	 *            the extraArgs to set
	 */
	public void setExtraArgs(String[] extraArgs) {
		this.extraArgs = extraArgs;
	}

	/**
	 * @return the extraArgs
	 */
	public String[] getExtraArgs() {
		return extraArgs;
	}

	/**
	 * @param waitSeconds
	 *            the waitSeconds to set
	 */
	public void setWaitSeconds(int waitSeconds) {
		this.waitSeconds = waitSeconds;
	}

	/**
	 * @return the waitSeconds
	 */
	public int getWaitSeconds() {
		return waitSeconds;
	}

	/**
	 * @param sleepMS
	 *            the sleepMS to set
	 */
	public void setSleepMS(int sleepMS) {
		this.sleepMS = sleepMS;
	}

	/**
	 * @return the sleepMS
	 */
	public int getSleepMS() {
		return sleepMS;
	}

	/**
	 * @param serverWorkerJar
	 *            the serverWorkerJar to set
	 */
	public void setServerWorkerJar(String serverWorkerJar) {
		this.serverWorkerJar = serverWorkerJar;
	}

	/**
	 * @return the serverWorkerJar
	 */
	public String getServerWorkerJar() {
		return serverWorkerJar;
	}

	/**
	 * @param javaBinary
	 *            the javaBinary to set
	 */
	public void setJavaBinary(String javaBinary) {
		this.javaBinary = javaBinary;
	}

	/**
	 * @return the javaBinary
	 */
	public String getJavaBinary() {
		return javaBinary;
	}

	/**
	 * @param registryPort
	 *            the registryPort to set
	 */
	public void setRegistryPort(int registryPort) {
		this.registryPort = registryPort;
	}

	/**
	 * @return the registryPort
	 */
	public int getRegistryPort() {
		return registryPort;
	}

	/**
	 * @param registryHost
	 *            the registryHost to set
	 */
	public void setRegistryHost(String registryHost) {
		this.registryHost = registryHost;
	}

	/**
	 * @return the registryHost
	 */
	public String getRegistryHost() {
		return registryHost;
	}
}