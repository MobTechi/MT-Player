import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.mobtech.mtplayer',
  appName: 'MT Player',
  webDir: 'www',
  server: {
    androidScheme: 'https'
  }
};

export default config;
