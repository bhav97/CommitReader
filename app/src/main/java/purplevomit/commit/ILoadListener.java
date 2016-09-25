package purplevomit.commit;

/**
 * Created by bhav on 9/23/16 for the CommitReader Project.
 */
public interface ILoadListener {
    boolean isLoading();

    void registerCallbacks(LoadingCallbacks callbacks);
    void unregisterCallbacks(LoadingCallbacks callbacks);

    interface LoadingCallbacks {
        void loadStarted();
        void loadFinished();
        void loadInterrupted();
    }
}
