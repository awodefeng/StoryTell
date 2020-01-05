package com.xxun.watch.storytall.systemUtil;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public abstract class MioAsyncTask<TParams, TProgress, TResult> {
	private Handler holder = new Handler(Looper.getMainLooper());
	private Thread exeThread = null;
	private TParams params[] = null;
	private volatile boolean canceled = false;
	private volatile boolean executing = false;
	private TResult result = null;
	private TProgress progresses[] = null;

	protected abstract TResult doInBackground(TParams... params);

	protected void onCancelled() {
		//LogUtil("mioAsyncTask onCancelled()");
	}

	protected void onPostExecute(TResult result) {
		//LogUtil("mioAsyncTask onPostExecute()");
	}

	protected void onPreExecute() {
		//LogUtil("mioAsyncTask onPreExecute()");
	}

	protected void onProgressUpdate(TProgress... values) {
		//LogUtil("mioAsyncTask onProgressUpdate()");

	}

	public final void publishProgress(TProgress... values) {
		//LogUtil("mioAsyncTask publishProgress()");
		if (exeThread == null || exeThread != Thread.currentThread()) {
			if (exeThread == null)
				Log.i("exeThread"," is null");
			else
				Log.i("can not pulish"," Progress in other thread.");
			return;
		}
		if (holder != null) {
			final TProgress valueArray[] = values;
			holder.post(new Runnable() {
				@Override
				public void run() {
					if (canceled == false) {
						onProgressUpdate(valueArray);
					}
				}
			});
		}
	}

	public final boolean cancel(boolean mayInterruptIfRunning) {
		if (exeThread != null && mayInterruptIfRunning == true) {
			if (canceled == false) {
				canceled = mayInterruptIfRunning;
			//	LogUtil.i("mioAsynTask cancel. thread_id=" + exeThread.getId());
				exeThread.interrupt();
				return true;
			}
		}

		return false;
	}

	public final void execute(TParams... params) {
		if (executing == true && exeThread != null) {
			Log.i("mioAsynTask"," already running.... thread_id="
					+ exeThread.getId());
			return;
		}

		//LogUtil.i("mioAsynTask execute() begin.");

		onPreExecute();
		this.params = params;

		Runnable task = new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				result = null;
				//LogUtil.i("enter mioAsynTask run()");
				try {
					result = doInBackground(MioAsyncTask.this.params);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (holder != null) {
					holder.post(new Runnable() {
						public void run() {
							executing = false; // 设置任务结束标志
							exeThread = null; // 回收thread
							//LogUtil.i("mioAsynTask in holder thread. thread_id=" + Thread.currentThread().getId());
							//LogUtil.i("canceled flag=" + canceled);
							if (canceled == true) {
								onCancelled();
							} else {
								onPostExecute(result);
							}
						}
					});
				}
				//LogUtil.i("exit mioAsynTask run()");
			}
		};

		executing = true;// 设置任务执行标志
		exeThread = new Thread(task);
		exeThread.start();

		//LogUtil("mioAsynTask execute() end.");
	}
}
