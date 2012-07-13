package cz.cuni.mff.d3s.deeco.knowledge.jini;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.jini.thread.Executor;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.AvailabilityEvent;
import cz.cuni.mff.d3s.deeco.exceptions.SessionException;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeRepository;
import cz.cuni.mff.d3s.deeco.scheduling.IKnowledgeChangeListener;

public class TSRemoteEventListener implements RemoteEventListener {

	private RemoteEventListener stub;
	private List<IKnowledgeChangeListener> toNotify;
	private String lastProcessed = null;
	private KnowledgeRepository kr;

	private TSRemoteEventListener() throws RemoteException {
		Exporter exporter = new BasicJeriExporter(
				TcpServerEndpoint.getInstance(0), new BasicILFactory(), false,
				true);
		stub = (RemoteEventListener) exporter.export(this);
		toNotify = new ArrayList<IKnowledgeChangeListener>();
	}

	public static TSRemoteEventListener getRemoteEventListener(
			KnowledgeRepository kr) {
		try {
			TSRemoteEventListener tsre = new TSRemoteEventListener();
			tsre.kr = kr;
			return tsre;
		} catch (Exception e) {
			return null;
		}

	}
	
	public RemoteEventListener getStub() {
		return stub;
	}

	public void addKCListener(IKnowledgeChangeListener listener) {
		if (!toNotify.contains(listener))
			toNotify.add(listener);
	}

	@Override
	public void notify(RemoteEvent re) throws UnknownEventException,
			RemoteException {
		TransactionalSession ts = null;
		try {
			AvailabilityEvent ae = (AvailabilityEvent) re;
			Tuple t = (Tuple) ae.getEntry();
			String currentVersion;
			ExecutorService es;
			ts = (TransactionalSession) kr.createSession();
			ts.begin();
			while (ts.repeat()) {
				currentVersion = (String) kr.get(t.key, ts);
				if (!currentVersion.equals(lastProcessed)) {
					es = Executors.newFixedThreadPool(toNotify.size());
					es.invokeAll(getTreadCollection());
					lastProcessed = currentVersion;
				}
				ts.end();
			}
		} catch (Exception e) {
			if (ts != null)
				ts.cancel();
			System.out.println("Notification exception: " + e.getMessage());
		}
	}

	private List<Callable<Object>> getTreadCollection() {
		List<Callable<Object>> result = new ArrayList<Callable<Object>>();
		for (IKnowledgeChangeListener ikcl : toNotify) {
			result.add(Executors.callable(new TriggeredThread(ikcl)));
		}
		return result;
	}

	class TriggeredThread implements Runnable {

		private IKnowledgeChangeListener ikcl;

		public TriggeredThread(IKnowledgeChangeListener ikcl) {
			this.ikcl = ikcl;
		}

		@Override
		public void run() {
			ikcl.knowledgeChanged();
		}

	}

}