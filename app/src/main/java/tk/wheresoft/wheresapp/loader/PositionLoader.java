package tk.wheresoft.wheresapp.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import tk.wheresoft.wheresapp.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio on 18/12/2014.
 */
public class PositionLoader extends AsyncTaskLoader<List<Message>> {

    private List<Message> messages;

    public PositionLoader(Context context) {
        super(context);
        messages = new ArrayList<>();
    }

    @Override
    public List<Message> loadInBackground() {
        return null;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<Message> messages) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (messages != null) {
                onReleaseResources(messages);
            }
        }
        List<Message> oldMessages = this.messages;
        this.messages = messages;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(messages);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldMessages != null) {
            onReleaseResources(oldMessages);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (messages != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(messages);
        }

        if (messages == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<Message> messages) {
        super.onCanceled(messages);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(messages);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (messages != null) {
            onReleaseResources(messages);
            messages = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<Message> contacts) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
