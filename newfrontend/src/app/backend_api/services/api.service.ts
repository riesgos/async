/* tslint:disable */
/* eslint-disable */
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpContext } from '@angular/common/http';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import { RequestBuilder } from '../request-builder';
import { Observable } from 'rxjs';
import { map, filter } from 'rxjs/operators';

import { BboxInput } from '../models/bbox-input';
import { ComplexInput } from '../models/complex-input';
import { ComplexInputAsValue } from '../models/complex-input-as-value';
import { ComplexOutput } from '../models/complex-output';
import { ComplexOutputAsInput } from '../models/complex-output-as-input';
import { Job } from '../models/job';
import { LiteralInput } from '../models/literal-input';
import { Order } from '../models/order';
import { OrderJobRef } from '../models/order-job-ref';
import { OrderPost } from '../models/order-post';
import { Process } from '../models/process';
import { Product } from '../models/product';
import { ProductType } from '../models/product-type';
import { User } from '../models/user';
import { UserCredentials } from '../models/user-credentials';
import { UserSelfInformation } from '../models/user-self-information';

@Injectable({
  providedIn: 'root',
})
export class ApiService extends BaseService {
  constructor(
    config: ApiConfiguration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * Path part for operation readListApiV1BboxInputsGet
   */
  static readonly ReadListApiV1BboxInputsGetPath = '/api/v1/bbox-inputs/';

  /**
   * Read List.
   *
   * Return the list of bbox inputs.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1BboxInputsGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1BboxInputsGet$Response(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: string;
    process_id?: string;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<BboxInput>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1BboxInputsGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('wps_identifier', params.wps_identifier, {});
      rb.query('job_id', params.job_id, {});
      rb.query('process_id', params.process_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<BboxInput>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of bbox inputs.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1BboxInputsGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1BboxInputsGet(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: string;
    process_id?: string;
    context?: HttpContext
  }
): Observable<Array<BboxInput>> {

    return this.readListApiV1BboxInputsGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<BboxInput>>) => r.body as Array<BboxInput>)
    );
  }

  /**
   * Path part for operation readDetailApiV1BboxInputsBboxInputIdGet
   */
  static readonly ReadDetailApiV1BboxInputsBboxInputIdGetPath = '/api/v1/bbox-inputs/{bbox_input_id}';

  /**
   * Read Detail.
   *
   * Return the bbox input with the given bbox input id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1BboxInputsBboxInputIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1BboxInputsBboxInputIdGet$Response(params: {
    bbox_input_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<BboxInput>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1BboxInputsBboxInputIdGetPath, 'get');
    if (params) {
      rb.path('bbox_input_id', params.bbox_input_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<BboxInput>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the bbox input with the given bbox input id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1BboxInputsBboxInputIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1BboxInputsBboxInputIdGet(params: {
    bbox_input_id: number;
    context?: HttpContext
  }
): Observable<BboxInput> {

    return this.readDetailApiV1BboxInputsBboxInputIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<BboxInput>) => r.body as BboxInput)
    );
  }

  /**
   * Path part for operation readListApiV1ComplexInputsGet
   */
  static readonly ReadListApiV1ComplexInputsGetPath = '/api/v1/complex-inputs/';

  /**
   * Read List.
   *
   * Return the list of complex inputs.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1ComplexInputsGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ComplexInputsGet$Response(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: number;
    process_id?: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<ComplexInput>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1ComplexInputsGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('wps_identifier', params.wps_identifier, {});
      rb.query('job_id', params.job_id, {});
      rb.query('process_id', params.process_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<ComplexInput>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of complex inputs.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1ComplexInputsGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ComplexInputsGet(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: number;
    process_id?: number;
    context?: HttpContext
  }
): Observable<Array<ComplexInput>> {

    return this.readListApiV1ComplexInputsGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<ComplexInput>>) => r.body as Array<ComplexInput>)
    );
  }

  /**
   * Path part for operation readDetailApiV1ComplexInputsComplexInputIdGet
   */
  static readonly ReadDetailApiV1ComplexInputsComplexInputIdGetPath = '/api/v1/complex-inputs/{complex_input_id}';

  /**
   * Read Detail.
   *
   * Return the complex input with the given complex input id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1ComplexInputsComplexInputIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ComplexInputsComplexInputIdGet$Response(params: {
    complex_input_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<ComplexInput>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1ComplexInputsComplexInputIdGetPath, 'get');
    if (params) {
      rb.path('complex_input_id', params.complex_input_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<ComplexInput>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the complex input with the given complex input id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1ComplexInputsComplexInputIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ComplexInputsComplexInputIdGet(params: {
    complex_input_id: number;
    context?: HttpContext
  }
): Observable<ComplexInput> {

    return this.readDetailApiV1ComplexInputsComplexInputIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<ComplexInput>) => r.body as ComplexInput)
    );
  }

  /**
   * Path part for operation readListApiV1ComplexInputsAsValuesGet
   */
  static readonly ReadListApiV1ComplexInputsAsValuesGetPath = '/api/v1/complex-inputs-as-values/';

  /**
   * Read List.
   *
   * Return the list of complex inputs.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1ComplexInputsAsValuesGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ComplexInputsAsValuesGet$Response(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: number;
    process_id?: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<ComplexInputAsValue>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1ComplexInputsAsValuesGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('wps_identifier', params.wps_identifier, {});
      rb.query('job_id', params.job_id, {});
      rb.query('process_id', params.process_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<ComplexInputAsValue>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of complex inputs.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1ComplexInputsAsValuesGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ComplexInputsAsValuesGet(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: number;
    process_id?: number;
    context?: HttpContext
  }
): Observable<Array<ComplexInputAsValue>> {

    return this.readListApiV1ComplexInputsAsValuesGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<ComplexInputAsValue>>) => r.body as Array<ComplexInputAsValue>)
    );
  }

  /**
   * Path part for operation readDetailApiV1ComplexInputsAsValuesComplexInputAsValueIdGet
   */
  static readonly ReadDetailApiV1ComplexInputsAsValuesComplexInputAsValueIdGetPath = '/api/v1/complex-inputs-as-values/{complex_input_as_value_id}';

  /**
   * Read Detail.
   *
   * Return the complex input with the given complex input id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1ComplexInputsAsValuesComplexInputAsValueIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ComplexInputsAsValuesComplexInputAsValueIdGet$Response(params: {
    complex_input_as_value_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<ComplexInputAsValue>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1ComplexInputsAsValuesComplexInputAsValueIdGetPath, 'get');
    if (params) {
      rb.path('complex_input_as_value_id', params.complex_input_as_value_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<ComplexInputAsValue>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the complex input with the given complex input id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1ComplexInputsAsValuesComplexInputAsValueIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ComplexInputsAsValuesComplexInputAsValueIdGet(params: {
    complex_input_as_value_id: number;
    context?: HttpContext
  }
): Observable<ComplexInputAsValue> {

    return this.readDetailApiV1ComplexInputsAsValuesComplexInputAsValueIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<ComplexInputAsValue>) => r.body as ComplexInputAsValue)
    );
  }

  /**
   * Path part for operation readListApiV1ComplexOutputsGet
   */
  static readonly ReadListApiV1ComplexOutputsGetPath = '/api/v1/complex-outputs/';

  /**
   * Read List.
   *
   * Return the list of complex outputs.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1ComplexOutputsGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ComplexOutputsGet$Response(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: number;
    process_id?: number;
    mime_type?: string;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<ComplexOutput>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1ComplexOutputsGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('wps_identifier', params.wps_identifier, {});
      rb.query('job_id', params.job_id, {});
      rb.query('process_id', params.process_id, {});
      rb.query('mime_type', params.mime_type, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<ComplexOutput>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of complex outputs.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1ComplexOutputsGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ComplexOutputsGet(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: number;
    process_id?: number;
    mime_type?: string;
    context?: HttpContext
  }
): Observable<Array<ComplexOutput>> {

    return this.readListApiV1ComplexOutputsGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<ComplexOutput>>) => r.body as Array<ComplexOutput>)
    );
  }

  /**
   * Path part for operation readDetailApiV1ComplexOutputsComplexOutputIdGet
   */
  static readonly ReadDetailApiV1ComplexOutputsComplexOutputIdGetPath = '/api/v1/complex-outputs/{complex_output_id}';

  /**
   * Read Detail.
   *
   * Return the complex output with the given complex output id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1ComplexOutputsComplexOutputIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ComplexOutputsComplexOutputIdGet$Response(params: {
    complex_output_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<ComplexOutput>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1ComplexOutputsComplexOutputIdGetPath, 'get');
    if (params) {
      rb.path('complex_output_id', params.complex_output_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<ComplexOutput>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the complex output with the given complex output id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1ComplexOutputsComplexOutputIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ComplexOutputsComplexOutputIdGet(params: {
    complex_output_id: number;
    context?: HttpContext
  }
): Observable<ComplexOutput> {

    return this.readDetailApiV1ComplexOutputsComplexOutputIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<ComplexOutput>) => r.body as ComplexOutput)
    );
  }

  /**
   * Path part for operation readListApiV1ComplexOutputsAsInputsGet
   */
  static readonly ReadListApiV1ComplexOutputsAsInputsGetPath = '/api/v1/complex-outputs-as-inputs/';

  /**
   * Read List.
   *
   * Return the list of complex outputs as inputs.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1ComplexOutputsAsInputsGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ComplexOutputsAsInputsGet$Response(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: number;
    process_id?: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<ComplexOutputAsInput>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1ComplexOutputsAsInputsGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('wps_identifier', params.wps_identifier, {});
      rb.query('job_id', params.job_id, {});
      rb.query('process_id', params.process_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<ComplexOutputAsInput>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of complex outputs as inputs.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1ComplexOutputsAsInputsGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ComplexOutputsAsInputsGet(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: number;
    process_id?: number;
    context?: HttpContext
  }
): Observable<Array<ComplexOutputAsInput>> {

    return this.readListApiV1ComplexOutputsAsInputsGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<ComplexOutputAsInput>>) => r.body as Array<ComplexOutputAsInput>)
    );
  }

  /**
   * Path part for operation readDetailApiV1ComplexOutputsAsInputsComplexOutputAsInputIdGet
   */
  static readonly ReadDetailApiV1ComplexOutputsAsInputsComplexOutputAsInputIdGetPath = '/api/v1/complex-outputs-as-inputs/{complex_output_as_input_id}';

  /**
   * Read Detail.
   *
   * Return the complex output with the given complex output as input id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1ComplexOutputsAsInputsComplexOutputAsInputIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ComplexOutputsAsInputsComplexOutputAsInputIdGet$Response(params: {
    complex_output_as_input_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<ComplexOutputAsInput>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1ComplexOutputsAsInputsComplexOutputAsInputIdGetPath, 'get');
    if (params) {
      rb.path('complex_output_as_input_id', params.complex_output_as_input_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<ComplexOutputAsInput>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the complex output with the given complex output as input id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1ComplexOutputsAsInputsComplexOutputAsInputIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ComplexOutputsAsInputsComplexOutputAsInputIdGet(params: {
    complex_output_as_input_id: number;
    context?: HttpContext
  }
): Observable<ComplexOutputAsInput> {

    return this.readDetailApiV1ComplexOutputsAsInputsComplexOutputAsInputIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<ComplexOutputAsInput>) => r.body as ComplexOutputAsInput)
    );
  }

  /**
   * Path part for operation readListApiV1JobsGet
   */
  static readonly ReadListApiV1JobsGetPath = '/api/v1/jobs/';

  /**
   * Read List.
   *
   * Return the list of jobs.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1JobsGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1JobsGet$Response(params?: {
    skip?: number;
    limit?: number;
    process_id?: number;
    status?: string;
    order_id?: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<Job>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1JobsGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('process_id', params.process_id, {});
      rb.query('status', params.status, {});
      rb.query('order_id', params.order_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<Job>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of jobs.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1JobsGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1JobsGet(params?: {
    skip?: number;
    limit?: number;
    process_id?: number;
    status?: string;
    order_id?: number;
    context?: HttpContext
  }
): Observable<Array<Job>> {

    return this.readListApiV1JobsGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<Job>>) => r.body as Array<Job>)
    );
  }

  /**
   * Path part for operation readDetailApiV1JobsJobIdGet
   */
  static readonly ReadDetailApiV1JobsJobIdGetPath = '/api/v1/jobs/{job_id}';

  /**
   * Read Detail.
   *
   * Return the job with the given job id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1JobsJobIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1JobsJobIdGet$Response(params: {
    job_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Job>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1JobsJobIdGetPath, 'get');
    if (params) {
      rb.path('job_id', params.job_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Job>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the job with the given job id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1JobsJobIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1JobsJobIdGet(params: {
    job_id: number;
    context?: HttpContext
  }
): Observable<Job> {

    return this.readDetailApiV1JobsJobIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<Job>) => r.body as Job)
    );
  }

  /**
   * Path part for operation readListApiV1LiteralInputsGet
   */
  static readonly ReadListApiV1LiteralInputsGetPath = '/api/v1/literal-inputs/';

  /**
   * Read List.
   *
   * Return the list of literal inputs.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1LiteralInputsGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1LiteralInputsGet$Response(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: number;
    process_id?: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<LiteralInput>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1LiteralInputsGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('wps_identifier', params.wps_identifier, {});
      rb.query('job_id', params.job_id, {});
      rb.query('process_id', params.process_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<LiteralInput>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of literal inputs.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1LiteralInputsGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1LiteralInputsGet(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    job_id?: number;
    process_id?: number;
    context?: HttpContext
  }
): Observable<Array<LiteralInput>> {

    return this.readListApiV1LiteralInputsGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<LiteralInput>>) => r.body as Array<LiteralInput>)
    );
  }

  /**
   * Path part for operation readDetailApiV1LiteralInputsLiteralInputIdGet
   */
  static readonly ReadDetailApiV1LiteralInputsLiteralInputIdGetPath = '/api/v1/literal-inputs/{literal_input_id}';

  /**
   * Read Detail.
   *
   * Return the literal input with the given literal input id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1LiteralInputsLiteralInputIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1LiteralInputsLiteralInputIdGet$Response(params: {
    literal_input_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<LiteralInput>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1LiteralInputsLiteralInputIdGetPath, 'get');
    if (params) {
      rb.path('literal_input_id', params.literal_input_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<LiteralInput>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the literal input with the given literal input id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1LiteralInputsLiteralInputIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1LiteralInputsLiteralInputIdGet(params: {
    literal_input_id: number;
    context?: HttpContext
  }
): Observable<LiteralInput> {

    return this.readDetailApiV1LiteralInputsLiteralInputIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<LiteralInput>) => r.body as LiteralInput)
    );
  }

  /**
   * Path part for operation readListApiV1OrderJobRefsGet
   */
  static readonly ReadListApiV1OrderJobRefsGetPath = '/api/v1/order-job-refs/';

  /**
   * Read List.
   *
   * Return the list of order job ref.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1OrderJobRefsGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1OrderJobRefsGet$Response(params?: {
    skip?: number;
    limit?: number;
    order_id?: number;
    job_id?: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<OrderJobRef>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1OrderJobRefsGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('order_id', params.order_id, {});
      rb.query('job_id', params.job_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<OrderJobRef>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of order job ref.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1OrderJobRefsGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1OrderJobRefsGet(params?: {
    skip?: number;
    limit?: number;
    order_id?: number;
    job_id?: number;
    context?: HttpContext
  }
): Observable<Array<OrderJobRef>> {

    return this.readListApiV1OrderJobRefsGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<OrderJobRef>>) => r.body as Array<OrderJobRef>)
    );
  }

  /**
   * Path part for operation readDetailApiV1OrderJobRefsOrderJobRefIdGet
   */
  static readonly ReadDetailApiV1OrderJobRefsOrderJobRefIdGetPath = '/api/v1/order-job-refs/{order_job_ref_id}';

  /**
   * Read Detail.
   *
   * Return the order with the given order job ref id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1OrderJobRefsOrderJobRefIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1OrderJobRefsOrderJobRefIdGet$Response(params: {
    order_job_ref_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<OrderJobRef>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1OrderJobRefsOrderJobRefIdGetPath, 'get');
    if (params) {
      rb.path('order_job_ref_id', params.order_job_ref_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<OrderJobRef>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the order with the given order job ref id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1OrderJobRefsOrderJobRefIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1OrderJobRefsOrderJobRefIdGet(params: {
    order_job_ref_id: number;
    context?: HttpContext
  }
): Observable<OrderJobRef> {

    return this.readDetailApiV1OrderJobRefsOrderJobRefIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<OrderJobRef>) => r.body as OrderJobRef)
    );
  }

  /**
   * Path part for operation readListApiV1OrdersGet
   */
  static readonly ReadListApiV1OrdersGetPath = '/api/v1/orders/';

  /**
   * Read List.
   *
   * Return the list of orders.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1OrdersGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1OrdersGet$Response(params?: {
    skip?: number;
    limit?: number;
    user_id?: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<Order>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1OrdersGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('user_id', params.user_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<Order>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of orders.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1OrdersGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1OrdersGet(params?: {
    skip?: number;
    limit?: number;
    user_id?: number;
    context?: HttpContext
  }
): Observable<Array<Order>> {

    return this.readListApiV1OrdersGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<Order>>) => r.body as Array<Order>)
    );
  }

  /**
   * Path part for operation createOrderApiV1OrdersPost
   */
  static readonly CreateOrderApiV1OrdersPostPath = '/api/v1/orders/';

  /**
   * Create Order.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `createOrderApiV1OrdersPost()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createOrderApiV1OrdersPost$Response(params: {
    'x-apikey'?: any;
    context?: HttpContext
    body: OrderPost
  }
): Observable<StrictHttpResponse<Order>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.CreateOrderApiV1OrdersPostPath, 'post');
    if (params) {
      rb.header('x-apikey', params['x-apikey'], {});
      rb.body(params.body, 'application/json');
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Order>;
      })
    );
  }

  /**
   * Create Order.
   *
   *
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `createOrderApiV1OrdersPost$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  createOrderApiV1OrdersPost(params: {
    'x-apikey'?: any;
    context?: HttpContext
    body: OrderPost
  }
): Observable<Order> {

    return this.createOrderApiV1OrdersPost$Response(params).pipe(
      map((r: StrictHttpResponse<Order>) => r.body as Order)
    );
  }

  /**
   * Path part for operation readDetailApiV1OrdersOrderIdGet
   */
  static readonly ReadDetailApiV1OrdersOrderIdGetPath = '/api/v1/orders/{order_id}';

  /**
   * Read Detail.
   *
   * Return the order with the given order id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1OrdersOrderIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1OrdersOrderIdGet$Response(params: {
    order_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Order>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1OrdersOrderIdGetPath, 'get');
    if (params) {
      rb.path('order_id', params.order_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Order>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the order with the given order id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1OrdersOrderIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1OrdersOrderIdGet(params: {
    order_id: number;
    context?: HttpContext
  }
): Observable<Order> {

    return this.readDetailApiV1OrdersOrderIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<Order>) => r.body as Order)
    );
  }

  /**
   * Path part for operation readListApiV1ProcessesGet
   */
  static readonly ReadListApiV1ProcessesGetPath = '/api/v1/processes/';

  /**
   * Read List.
   *
   * Return the list of processes.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1ProcessesGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ProcessesGet$Response(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    wps_url?: string;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<Process>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1ProcessesGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('wps_identifier', params.wps_identifier, {});
      rb.query('wps_url', params.wps_url, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<Process>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of processes.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1ProcessesGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ProcessesGet(params?: {
    skip?: number;
    limit?: number;
    wps_identifier?: string;
    wps_url?: string;
    context?: HttpContext
  }
): Observable<Array<Process>> {

    return this.readListApiV1ProcessesGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<Process>>) => r.body as Array<Process>)
    );
  }

  /**
   * Path part for operation readDetailApiV1ProcessesProcessIdGet
   */
  static readonly ReadDetailApiV1ProcessesProcessIdGetPath = '/api/v1/processes/{process_id}';

  /**
   * Read Detail.
   *
   * Return the process with the given process id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1ProcessesProcessIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ProcessesProcessIdGet$Response(params: {
    process_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Process>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1ProcessesProcessIdGetPath, 'get');
    if (params) {
      rb.path('process_id', params.process_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Process>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the process with the given process id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1ProcessesProcessIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ProcessesProcessIdGet(params: {
    process_id: number;
    context?: HttpContext
  }
): Observable<Process> {

    return this.readDetailApiV1ProcessesProcessIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<Process>) => r.body as Process)
    );
  }

  /**
   * Path part for operation readListApiV1ProductsGet
   */
  static readonly ReadListApiV1ProductsGetPath = '/api/v1/products/';

  /**
   * Read List.
   *
   * Return the list of products.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1ProductsGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ProductsGet$Response(params?: {
    skip?: number;
    limit?: number;
    product_type_id?: number;
    order_id?: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<Product>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1ProductsGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.query('product_type_id', params.product_type_id, {});
      rb.query('order_id', params.order_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<Product>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of products.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1ProductsGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ProductsGet(params?: {
    skip?: number;
    limit?: number;
    product_type_id?: number;
    order_id?: number;
    context?: HttpContext
  }
): Observable<Array<Product>> {

    return this.readListApiV1ProductsGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<Product>>) => r.body as Array<Product>)
    );
  }

  /**
   * Path part for operation readDetailApiV1ProductsProductIdGet
   */
  static readonly ReadDetailApiV1ProductsProductIdGetPath = '/api/v1/products/{product_id}';

  /**
   * Read Detail.
   *
   * Return the job with the given product id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1ProductsProductIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ProductsProductIdGet$Response(params: {
    product_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Product>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1ProductsProductIdGetPath, 'get');
    if (params) {
      rb.path('product_id', params.product_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Product>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the job with the given product id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1ProductsProductIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ProductsProductIdGet(params: {
    product_id: number;
    context?: HttpContext
  }
): Observable<Product> {

    return this.readDetailApiV1ProductsProductIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<Product>) => r.body as Product)
    );
  }

  /**
   * Path part for operation readDervicedProductsApiV1ProductsProductIdDerivedProductsGet
   */
  static readonly ReadDervicedProductsApiV1ProductsProductIdDerivedProductsGetPath = '/api/v1/products/{product_id}/derived-products';

  /**
   * Read Derviced Products.
   *
   * Return the list of derived products.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDervicedProductsApiV1ProductsProductIdDerivedProductsGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDervicedProductsApiV1ProductsProductIdDerivedProductsGet$Response(params: {
    product_id: number;
    skip?: any;
    limit?: any;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<Product>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDervicedProductsApiV1ProductsProductIdDerivedProductsGetPath, 'get');
    if (params) {
      rb.path('product_id', params.product_id, {});
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<Product>>;
      })
    );
  }

  /**
   * Read Derviced Products.
   *
   * Return the list of derived products.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDervicedProductsApiV1ProductsProductIdDerivedProductsGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDervicedProductsApiV1ProductsProductIdDerivedProductsGet(params: {
    product_id: number;
    skip?: any;
    limit?: any;
    context?: HttpContext
  }
): Observable<Array<Product>> {

    return this.readDervicedProductsApiV1ProductsProductIdDerivedProductsGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<Product>>) => r.body as Array<Product>)
    );
  }

  /**
   * Path part for operation readBaseProductsApiV1ProductsProductIdBaseProductsGet
   */
  static readonly ReadBaseProductsApiV1ProductsProductIdBaseProductsGetPath = '/api/v1/products/{product_id}/base-products';

  /**
   * Read Base Products.
   *
   * Return the list of base products.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readBaseProductsApiV1ProductsProductIdBaseProductsGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readBaseProductsApiV1ProductsProductIdBaseProductsGet$Response(params: {
    product_id: number;
    skip?: any;
    limit?: any;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<Product>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadBaseProductsApiV1ProductsProductIdBaseProductsGetPath, 'get');
    if (params) {
      rb.path('product_id', params.product_id, {});
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<Product>>;
      })
    );
  }

  /**
   * Read Base Products.
   *
   * Return the list of base products.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readBaseProductsApiV1ProductsProductIdBaseProductsGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readBaseProductsApiV1ProductsProductIdBaseProductsGet(params: {
    product_id: number;
    skip?: any;
    limit?: any;
    context?: HttpContext
  }
): Observable<Array<Product>> {

    return this.readBaseProductsApiV1ProductsProductIdBaseProductsGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<Product>>) => r.body as Array<Product>)
    );
  }

  /**
   * Path part for operation readListApiV1ProductTypesGet
   */
  static readonly ReadListApiV1ProductTypesGetPath = '/api/v1/product-types/';

  /**
   * Read List.
   *
   * Return the list of product types.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1ProductTypesGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ProductTypesGet$Response(params?: {
    skip?: number;
    limit?: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<ProductType>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1ProductTypesGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<ProductType>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of product types.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1ProductTypesGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1ProductTypesGet(params?: {
    skip?: number;
    limit?: number;
    context?: HttpContext
  }
): Observable<Array<ProductType>> {

    return this.readListApiV1ProductTypesGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<ProductType>>) => r.body as Array<ProductType>)
    );
  }

  /**
   * Path part for operation readDetailApiV1ProductTypesProductTypeIdGet
   */
  static readonly ReadDetailApiV1ProductTypesProductTypeIdGetPath = '/api/v1/product-types/{product_type_id}';

  /**
   * Read Detail.
   *
   * Return the job with the given product type id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1ProductTypesProductTypeIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ProductTypesProductTypeIdGet$Response(params: {
    product_type_id: number;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<ProductType>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1ProductTypesProductTypeIdGetPath, 'get');
    if (params) {
      rb.path('product_type_id', params.product_type_id, {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<ProductType>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the job with the given product type id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1ProductTypesProductTypeIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1ProductTypesProductTypeIdGet(params: {
    product_type_id: number;
    context?: HttpContext
  }
): Observable<ProductType> {

    return this.readDetailApiV1ProductTypesProductTypeIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<ProductType>) => r.body as ProductType)
    );
  }

  /**
   * Path part for operation readListApiV1UsersGet
   */
  static readonly ReadListApiV1UsersGetPath = '/api/v1/users/';

  /**
   * Read List.
   *
   * Return the list of users.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readListApiV1UsersGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1UsersGet$Response(params?: {
    skip?: number;
    limit?: number;
    'x-apikey'?: any;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<Array<User>>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadListApiV1UsersGetPath, 'get');
    if (params) {
      rb.query('skip', params.skip, {});
      rb.query('limit', params.limit, {});
      rb.header('x-apikey', params['x-apikey'], {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<Array<User>>;
      })
    );
  }

  /**
   * Read List.
   *
   * Return the list of users.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readListApiV1UsersGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readListApiV1UsersGet(params?: {
    skip?: number;
    limit?: number;
    'x-apikey'?: any;
    context?: HttpContext
  }
): Observable<Array<User>> {

    return this.readListApiV1UsersGet$Response(params).pipe(
      map((r: StrictHttpResponse<Array<User>>) => r.body as Array<User>)
    );
  }

  /**
   * Path part for operation readDetailApiV1UsersUserIdGet
   */
  static readonly ReadDetailApiV1UsersUserIdGetPath = '/api/v1/users/{user_id}';

  /**
   * Read Detail.
   *
   * Return the user with the given user id or raise 404.
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `readDetailApiV1UsersUserIdGet()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1UsersUserIdGet$Response(params: {
    user_id: number;
    'x-apikey'?: any;
    context?: HttpContext
  }
): Observable<StrictHttpResponse<User>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.ReadDetailApiV1UsersUserIdGetPath, 'get');
    if (params) {
      rb.path('user_id', params.user_id, {});
      rb.header('x-apikey', params['x-apikey'], {});
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<User>;
      })
    );
  }

  /**
   * Read Detail.
   *
   * Return the user with the given user id or raise 404.
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `readDetailApiV1UsersUserIdGet$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  readDetailApiV1UsersUserIdGet(params: {
    user_id: number;
    'x-apikey'?: any;
    context?: HttpContext
  }
): Observable<User> {

    return this.readDetailApiV1UsersUserIdGet$Response(params).pipe(
      map((r: StrictHttpResponse<User>) => r.body as User)
    );
  }

  /**
   * Path part for operation registerUserApiV1UsersRegisterPost
   */
  static readonly RegisterUserApiV1UsersRegisterPostPath = '/api/v1/users/register';

  /**
   * Register User.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `registerUserApiV1UsersRegisterPost()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  registerUserApiV1UsersRegisterPost$Response(params: {
    context?: HttpContext
    body: UserCredentials
  }
): Observable<StrictHttpResponse<UserSelfInformation>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.RegisterUserApiV1UsersRegisterPostPath, 'post');
    if (params) {
      rb.body(params.body, 'application/json');
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<UserSelfInformation>;
      })
    );
  }

  /**
   * Register User.
   *
   *
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `registerUserApiV1UsersRegisterPost$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  registerUserApiV1UsersRegisterPost(params: {
    context?: HttpContext
    body: UserCredentials
  }
): Observable<UserSelfInformation> {

    return this.registerUserApiV1UsersRegisterPost$Response(params).pipe(
      map((r: StrictHttpResponse<UserSelfInformation>) => r.body as UserSelfInformation)
    );
  }

  /**
   * Path part for operation loginUserApiV1UsersLoginPost
   */
  static readonly LoginUserApiV1UsersLoginPostPath = '/api/v1/users/login';

  /**
   * Login User.
   *
   *
   *
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `loginUserApiV1UsersLoginPost()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  loginUserApiV1UsersLoginPost$Response(params: {
    context?: HttpContext
    body: UserCredentials
  }
): Observable<StrictHttpResponse<UserSelfInformation>> {

    const rb = new RequestBuilder(this.rootUrl, ApiService.LoginUserApiV1UsersLoginPostPath, 'post');
    if (params) {
      rb.body(params.body, 'application/json');
    }

    return this.http.request(rb.build({
      responseType: 'json',
      accept: 'application/json',
      context: params?.context
    })).pipe(
      filter((r: any) => r instanceof HttpResponse),
      map((r: HttpResponse<any>) => {
        return r as StrictHttpResponse<UserSelfInformation>;
      })
    );
  }

  /**
   * Login User.
   *
   *
   *
   * This method provides access to only to the response body.
   * To access the full response (for headers, for example), `loginUserApiV1UsersLoginPost$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  loginUserApiV1UsersLoginPost(params: {
    context?: HttpContext
    body: UserCredentials
  }
): Observable<UserSelfInformation> {

    return this.loginUserApiV1UsersLoginPost$Response(params).pipe(
      map((r: StrictHttpResponse<UserSelfInformation>) => r.body as UserSelfInformation)
    );
  }

}
