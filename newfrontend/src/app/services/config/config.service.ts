import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

export interface AppConfig {
  filestorageUrl: string
}

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  
  protected config: AppConfig = {
    filestorageUrl: 'initial-value-filstorage-url'
  };

  constructor() {}

  public async loadConfig(): Promise<true> {
      // Must not use angular's http-client.
      // Reason: http-client depends on HTTP_INTERCEPTORS
      // But our ProxyInterceptor depends on ConfigService ...
      // That's a circular dependency.

      const type = environment.production ? 'prod' : 'dev';
      console.log(`Loading config for ${type}...`);
      const result = await fetch(`assets/config.${type}.json`);
      this.config = await result.json();
      console.log("... got config:", this.config);
      return true;
  }

  public getConfig() {
      return this.config;
  }

}
