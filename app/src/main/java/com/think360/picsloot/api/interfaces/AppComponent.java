package com.think360.picsloot.api.interfaces;

import com.think360.picsloot.activities.LoginActivity;
import com.think360.picsloot.activities.PicsLootActivity;
import com.think360.picsloot.api.ApplicationModule;
import com.think360.picsloot.api.HttpModule;
import com.think360.picsloot.fragments.CompleteDeliveryOrderAddFragment;
import com.think360.picsloot.fragments.EditDeliveryAddressFragment;
import com.think360.picsloot.fragments.EnterMobileNumberFragment;
import com.think360.picsloot.fragments.FinishDeliveryOrderFragment;
import com.think360.picsloot.fragments.HomeFragment;
import com.think360.picsloot.fragments.NotificationFragment;
import com.think360.picsloot.fragments.OrderFragment;
import com.think360.picsloot.fragments.ProfileFragment;
import com.think360.picsloot.fragments.ProfileSaveUploadFragment;
import com.think360.pro.healthguru.registration.fragments.OtpVerificationFragment;
import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {HttpModule.class, ApplicationModule.class})
public interface AppComponent {
        void inject(LoginActivity loginActivity);
        void inject(PicsLootActivity fragment);
        void inject(EnterMobileNumberFragment fragment);
        void inject(OtpVerificationFragment fragment);
        void inject(ProfileSaveUploadFragment fragment);
        void inject(HomeFragment fragment);
        void inject(OrderFragment fragment);
        void inject(ProfileFragment fragment);
        void inject(EditDeliveryAddressFragment fragment);
        void inject(CompleteDeliveryOrderAddFragment fragment);
        void inject(FinishDeliveryOrderFragment fragment);
        void inject(NotificationFragment fragment);
}
