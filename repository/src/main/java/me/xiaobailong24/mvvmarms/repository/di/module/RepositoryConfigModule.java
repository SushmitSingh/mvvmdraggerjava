package me.xiaobailong24.mvvmarms.repository.di.module;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import me.xiaobailong24.mvvmarms.repository.cache.Cache;
import me.xiaobailong24.mvvmarms.repository.cache.CacheType;
import me.xiaobailong24.mvvmarms.repository.cache.LruCache;
import me.xiaobailong24.mvvmarms.repository.http.BaseUrl;
import me.xiaobailong24.mvvmarms.repository.http.GlobalHttpHandler;
import me.xiaobailong24.mvvmarms.repository.utils.DataHelper;
import me.xiaobailong24.mvvmarms.repository.http.RequestInterceptor;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;

/**
 * @author xiaobailong24
 * @date 2017/9/28
 * RepositoryConfigModule
 */
@Module
public class RepositoryConfigModule {
    private Application mApplication;
    private HttpUrl mApiUrl;
    private BaseUrl mBaseUrl;
    private File mCacheFile;
    private GlobalHttpHandler mHandler;
    private List<Interceptor> mInterceptors;
    private ResponseErrorListener mErrorListener;
    private ClientModule.RetrofitConfiguration mRetrofitConfiguration;
    private ClientModule.OkhttpConfiguration mOkhttpConfiguration;
    private ClientModule.GsonConfiguration mGsonConfiguration;
    private ClientModule.RxCacheConfiguration mRxCacheConfiguration;
    private RequestInterceptor.Level mPrintHttpLogLevel;
    private DatabaseModule.RoomConfiguration mRoomConfiguration;
    private Cache.Factory mCacheFactory;


    private RepositoryConfigModule(Builder builder) {
        this.mApplication = builder.application;
        this.mApiUrl = builder.apiUrl;
        this.mBaseUrl = builder.baseUrl;
        this.mHandler = builder.handler;
        this.mCacheFile = builder.cacheFile;
        this.mInterceptors = builder.interceptors;
        this.mErrorListener = builder.responseErrorListener;
        this.mRetrofitConfiguration = builder.retrofitConfiguration;
        this.mOkhttpConfiguration = builder.okhttpConfiguration;
        this.mGsonConfiguration = builder.gsonConfiguration;
        this.mRxCacheConfiguration = builder.rxCacheConfiguration;
        this.mPrintHttpLogLevel = builder.printHttpLogLevel;
        this.mRoomConfiguration = builder.roomConfiguration;
        this.mCacheFactory = builder.cacheFactory;
    }

    public static Builder builder() {
        return new Builder();
    }


    @Singleton
    @Provides
    @Nullable
    List<Interceptor> provideInterceptors() {
        return mInterceptors;
    }


    @Singleton
    @Provides
    HttpUrl provideBaseUrl() {
        if (mBaseUrl != null) {
            HttpUrl httpUrl = mBaseUrl.url();
            if (httpUrl != null) {
                return httpUrl;
            }
        }
        return mApiUrl == null ? HttpUrl.parse("https://api.github.com/") : mApiUrl;
    }

    @Singleton
    @Provides
    File provideCacheFile() {
        //??????????????????
        return mCacheFile == null ? DataHelper.getCacheFile(mApplication) : mCacheFile;
    }

    @Singleton
    @Provides
    @Nullable
    GlobalHttpHandler provideGlobalHttpHandler() {
        return mHandler;//??????Http?????????????????????
    }


    @Singleton
    @Provides
    ResponseErrorListener provideResponseErrorListener() {
        return mErrorListener == null ? ResponseErrorListener.EMPTY : mErrorListener;
    }

    @Singleton
    @Provides
    @Nullable
    ClientModule.RetrofitConfiguration provideRetrofitConfiguration() {
        return mRetrofitConfiguration;
    }

    @Singleton
    @Provides
    @Nullable
    ClientModule.OkhttpConfiguration provideOkhttpConfiguration() {
        return mOkhttpConfiguration;
    }

    @Singleton
    @Provides
    @Nullable
    ClientModule.GsonConfiguration provideGsonConfiguration() {
        return mGsonConfiguration;
    }

    @Singleton
    @Provides
    @Nullable
    ClientModule.RxCacheConfiguration provideRxCacheConfiguration() {
        return mRxCacheConfiguration;
    }

    @Singleton
    @Provides
    @Nullable
    RequestInterceptor.Level providePrintHttpLogLevel() {
        return mPrintHttpLogLevel;
    }

    @Singleton
    @Provides
    DatabaseModule.RoomConfiguration provideRoomConfiguration() {
        return mRoomConfiguration == null ? DatabaseModule.RoomConfiguration.EMPTY : mRoomConfiguration;
    }

    @Singleton
    @Provides
    Cache.Factory provideCacheFactory() {
        return mCacheFactory == null ? new Cache.Factory() {
            @NonNull
            @Override
            public Cache build(CacheType type) {
                //??????????????? LruCache ??? size,?????????????????? LruCache ,?????????????????????????????????
                //????????? RepositoryConfigModule.Builder#cacheFactory() ??????
                switch (type) {
                    case EXTRAS_CACHE_TYPE:
                        //?????? extras ????????????????????????500?????????
                        return new LruCache(500);
                    default:
                        //RepositoryManager ???????????????????????? 100 ?????????
                        return new LruCache(Cache.Factory.DEFAULT_CACHE_SIZE);
                }
            }
        } : mCacheFactory;
    }

    public static final class Builder {
        private Application application;
        private HttpUrl apiUrl;
        private BaseUrl baseUrl;
        private File cacheFile;
        private GlobalHttpHandler handler;
        private List<Interceptor> interceptors;
        private ResponseErrorListener responseErrorListener;
        private ClientModule.RetrofitConfiguration retrofitConfiguration;
        private ClientModule.OkhttpConfiguration okhttpConfiguration;
        private ClientModule.GsonConfiguration gsonConfiguration;
        private ClientModule.RxCacheConfiguration rxCacheConfiguration;
        private RequestInterceptor.Level printHttpLogLevel;
        private DatabaseModule.RoomConfiguration roomConfiguration;
        private Cache.Factory cacheFactory;


        private Builder() {
        }

        @NonNull
        public Builder application(Application application) {
            this.application = application;
            return this;
        }

        public Builder baseUrl(String baseUrl) {//??????url
            if (TextUtils.isEmpty(baseUrl)) {
                throw new IllegalArgumentException("BaseUrl can not be empty");
            }
            this.apiUrl = HttpUrl.parse(baseUrl);
            return this;
        }

        public Builder baseUrl(BaseUrl baseUrl) {
            if (baseUrl == null) {
                throw new IllegalArgumentException("BaseUrl can not be null");
            }
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder cacheFile(File cacheFile) {
            this.cacheFile = cacheFile;
            return this;
        }

        public Builder globalHttpHandler(GlobalHttpHandler handler) {//????????????http????????????
            this.handler = handler;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {//?????????????????????interceptor
            if (interceptors == null)
                interceptors = new ArrayList<>();
            this.interceptors.add(interceptor);
            return this;
        }

        public Builder responseErrorListener(ResponseErrorListener listener) {//????????????Rxjava???onError??????
            this.responseErrorListener = listener;
            return this;
        }

        public Builder retrofitConfiguration(ClientModule.RetrofitConfiguration retrofitConfiguration) {
            this.retrofitConfiguration = retrofitConfiguration;
            return this;
        }

        public Builder okhttpConfiguration(ClientModule.OkhttpConfiguration okhttpConfiguration) {
            this.okhttpConfiguration = okhttpConfiguration;
            return this;
        }

        public Builder gsonConfiguration(ClientModule.GsonConfiguration gsonConfiguration) {
            this.gsonConfiguration = gsonConfiguration;
            return this;
        }

        public Builder rxCacheConfiguration(ClientModule.RxCacheConfiguration rxCacheConfiguration) {
            this.rxCacheConfiguration = rxCacheConfiguration;
            return this;
        }

        public Builder printHttpLogLevel(RequestInterceptor.Level printHttpLogLevel) { //????????????????????? Http ????????????????????????
            if (printHttpLogLevel == null)
                throw new IllegalArgumentException("printHttpLogLevel == null. Use RequestInterceptor.Level.NONE instead.");
            this.printHttpLogLevel = printHttpLogLevel;
            return this;
        }

        public Builder roomConfiguration(DatabaseModule.RoomConfiguration roomConfiguration) {
            this.roomConfiguration = roomConfiguration;
            return this;
        }

        public Builder cacheFactory(Cache.Factory cacheFactory) {
            this.cacheFactory = cacheFactory;
            return this;
        }


        public RepositoryConfigModule build() {
            return new RepositoryConfigModule(this);
        }

    }
}
