/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BboxInput } from '../models/BboxInput';
import type { ComplexInput } from '../models/ComplexInput';
import type { ComplexInputAsValue } from '../models/ComplexInputAsValue';
import type { ComplexOutput } from '../models/ComplexOutput';
import type { ComplexOutputAsInput } from '../models/ComplexOutputAsInput';
import type { Job } from '../models/Job';
import type { LiteralInput } from '../models/LiteralInput';
import type { Order } from '../models/Order';
import type { OrderJobRef } from '../models/OrderJobRef';
import type { OrderPost } from '../models/OrderPost';
import type { Process } from '../models/Process';
import type { Product } from '../models/Product';
import type { ProductType } from '../models/ProductType';
import type { User } from '../models/User';
import type { UserCredentials } from '../models/UserCredentials';
import type { UserSelfInformation } from '../models/UserSelfInformation';

import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';

export class DefaultService {

    /**
     * Read List
     * Return the list of bbox inputs.
     * @param skip
     * @param limit
     * @param wpsIdentifier
     * @param jobId
     * @param processId
     * @returns BboxInput Successful Response
     * @throws ApiError
     */
    public static readListApiV1BboxInputsGet(
        skip?: number,
        limit: number = 100,
        wpsIdentifier?: string,
        jobId?: string,
        processId?: string,
    ): CancelablePromise<Array<BboxInput>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/bbox-inputs/',
            query: {
                'skip': skip,
                'limit': limit,
                'wps_identifier': wpsIdentifier,
                'job_id': jobId,
                'process_id': processId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the bbox input with the given bbox input id or raise 404.
     * @param bboxInputId
     * @returns BboxInput Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1BboxInputsBboxInputIdGet(
        bboxInputId: number,
    ): CancelablePromise<BboxInput> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/bbox-inputs/{bbox_input_id}',
            path: {
                'bbox_input_id': bboxInputId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of complex inputs.
     * @param skip
     * @param limit
     * @param wpsIdentifier
     * @param jobId
     * @param processId
     * @returns ComplexInput Successful Response
     * @throws ApiError
     */
    public static readListApiV1ComplexInputsGet(
        skip?: number,
        limit: number = 100,
        wpsIdentifier?: string,
        jobId?: number,
        processId?: number,
    ): CancelablePromise<Array<ComplexInput>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/complex-inputs/',
            query: {
                'skip': skip,
                'limit': limit,
                'wps_identifier': wpsIdentifier,
                'job_id': jobId,
                'process_id': processId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the complex input with the given complex input id or raise 404.
     * @param complexInputId
     * @returns ComplexInput Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1ComplexInputsComplexInputIdGet(
        complexInputId: number,
    ): CancelablePromise<ComplexInput> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/complex-inputs/{complex_input_id}',
            path: {
                'complex_input_id': complexInputId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of complex inputs.
     * @param skip
     * @param limit
     * @param wpsIdentifier
     * @param jobId
     * @param processId
     * @returns ComplexInputAsValue Successful Response
     * @throws ApiError
     */
    public static readListApiV1ComplexInputsAsValuesGet(
        skip?: number,
        limit: number = 100,
        wpsIdentifier?: string,
        jobId?: number,
        processId?: number,
    ): CancelablePromise<Array<ComplexInputAsValue>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/complex-inputs-as-values/',
            query: {
                'skip': skip,
                'limit': limit,
                'wps_identifier': wpsIdentifier,
                'job_id': jobId,
                'process_id': processId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the complex input with the given complex input id or raise 404.
     * @param complexInputAsValueId
     * @returns ComplexInputAsValue Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1ComplexInputsAsValuesComplexInputAsValueIdGet(
        complexInputAsValueId: number,
    ): CancelablePromise<ComplexInputAsValue> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/complex-inputs-as-values/{complex_input_as_value_id}',
            path: {
                'complex_input_as_value_id': complexInputAsValueId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of complex outputs.
     * @param skip
     * @param limit
     * @param wpsIdentifier
     * @param jobId
     * @param processId
     * @param mimeType
     * @returns ComplexOutput Successful Response
     * @throws ApiError
     */
    public static readListApiV1ComplexOutputsGet(
        skip?: number,
        limit: number = 100,
        wpsIdentifier?: string,
        jobId?: number,
        processId?: number,
        mimeType?: string,
    ): CancelablePromise<Array<ComplexOutput>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/complex-outputs/',
            query: {
                'skip': skip,
                'limit': limit,
                'wps_identifier': wpsIdentifier,
                'job_id': jobId,
                'process_id': processId,
                'mime_type': mimeType,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the complex output with the given complex output id or raise 404.
     * @param complexOutputId
     * @returns ComplexOutput Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1ComplexOutputsComplexOutputIdGet(
        complexOutputId: number,
    ): CancelablePromise<ComplexOutput> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/complex-outputs/{complex_output_id}',
            path: {
                'complex_output_id': complexOutputId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of complex outputs as inputs.
     * @param skip
     * @param limit
     * @param wpsIdentifier
     * @param jobId
     * @param processId
     * @returns ComplexOutputAsInput Successful Response
     * @throws ApiError
     */
    public static readListApiV1ComplexOutputsAsInputsGet(
        skip?: number,
        limit: number = 100,
        wpsIdentifier?: string,
        jobId?: number,
        processId?: number,
    ): CancelablePromise<Array<ComplexOutputAsInput>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/complex-outputs-as-inputs/',
            query: {
                'skip': skip,
                'limit': limit,
                'wps_identifier': wpsIdentifier,
                'job_id': jobId,
                'process_id': processId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the complex output with the given complex output as input id or raise 404.
     * @param complexOutputAsInputId
     * @returns ComplexOutputAsInput Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1ComplexOutputsAsInputsComplexOutputAsInputIdGet(
        complexOutputAsInputId: number,
    ): CancelablePromise<ComplexOutputAsInput> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/complex-outputs-as-inputs/{complex_output_as_input_id}',
            path: {
                'complex_output_as_input_id': complexOutputAsInputId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of jobs.
     * @param skip
     * @param limit
     * @param processId
     * @param status
     * @param orderId
     * @returns Job Successful Response
     * @throws ApiError
     */
    public static readListApiV1JobsGet(
        skip?: number,
        limit: number = 100,
        processId?: number,
        status?: string,
        orderId?: number,
    ): CancelablePromise<Array<Job>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/jobs/',
            query: {
                'skip': skip,
                'limit': limit,
                'process_id': processId,
                'status': status,
                'order_id': orderId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the job with the given job id or raise 404.
     * @param jobId
     * @returns Job Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1JobsJobIdGet(
        jobId: number,
    ): CancelablePromise<Job> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/jobs/{job_id}',
            path: {
                'job_id': jobId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of literal inputs.
     * @param skip
     * @param limit
     * @param wpsIdentifier
     * @param jobId
     * @param processId
     * @returns LiteralInput Successful Response
     * @throws ApiError
     */
    public static readListApiV1LiteralInputsGet(
        skip?: number,
        limit: number = 100,
        wpsIdentifier?: string,
        jobId?: number,
        processId?: number,
    ): CancelablePromise<Array<LiteralInput>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/literal-inputs/',
            query: {
                'skip': skip,
                'limit': limit,
                'wps_identifier': wpsIdentifier,
                'job_id': jobId,
                'process_id': processId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the literal input with the given literal input id or raise 404.
     * @param literalInputId
     * @returns LiteralInput Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1LiteralInputsLiteralInputIdGet(
        literalInputId: number,
    ): CancelablePromise<LiteralInput> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/literal-inputs/{literal_input_id}',
            path: {
                'literal_input_id': literalInputId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of order job ref.
     * @param skip
     * @param limit
     * @param orderId
     * @param jobId
     * @returns OrderJobRef Successful Response
     * @throws ApiError
     */
    public static readListApiV1OrderJobRefsGet(
        skip?: number,
        limit: number = 100,
        orderId?: number,
        jobId?: number,
    ): CancelablePromise<Array<OrderJobRef>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/order-job-refs/',
            query: {
                'skip': skip,
                'limit': limit,
                'order_id': orderId,
                'job_id': jobId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the order with the given order job ref id or raise 404.
     * @param orderJobRefId
     * @returns OrderJobRef Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1OrderJobRefsOrderJobRefIdGet(
        orderJobRefId: number,
    ): CancelablePromise<OrderJobRef> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/order-job-refs/{order_job_ref_id}',
            path: {
                'order_job_ref_id': orderJobRefId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of orders.
     * @param skip
     * @param limit
     * @param userId
     * @returns Order Successful Response
     * @throws ApiError
     */
    public static readListApiV1OrdersGet(
        skip?: number,
        limit: number = 100,
        userId?: number,
    ): CancelablePromise<Array<Order>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/orders/',
            query: {
                'skip': skip,
                'limit': limit,
                'user_id': userId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Create Order
     * @param requestBody
     * @param xApikey
     * @returns Order Successful Response
     * @throws ApiError
     */
    public static createOrderApiV1OrdersPost(
        requestBody: OrderPost,
        xApikey?: any,
    ): CancelablePromise<Order> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/orders/',
            headers: {
                'x-apikey': xApikey,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the order with the given order id or raise 404.
     * @param orderId
     * @returns Order Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1OrdersOrderIdGet(
        orderId: number,
    ): CancelablePromise<Order> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/orders/{order_id}',
            path: {
                'order_id': orderId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of processes.
     * @param skip
     * @param limit
     * @param wpsIdentifier
     * @param wpsUrl
     * @returns Process Successful Response
     * @throws ApiError
     */
    public static readListApiV1ProcessesGet(
        skip?: number,
        limit: number = 100,
        wpsIdentifier?: string,
        wpsUrl?: string,
    ): CancelablePromise<Array<Process>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/processes/',
            query: {
                'skip': skip,
                'limit': limit,
                'wps_identifier': wpsIdentifier,
                'wps_url': wpsUrl,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the process with the given process id or raise 404.
     * @param processId
     * @returns Process Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1ProcessesProcessIdGet(
        processId: number,
    ): CancelablePromise<Process> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/processes/{process_id}',
            path: {
                'process_id': processId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of products.
     * @param skip
     * @param limit
     * @param productTypeId
     * @param orderId
     * @returns Product Successful Response
     * @throws ApiError
     */
    public static readListApiV1ProductsGet(
        skip?: number,
        limit: number = 100,
        productTypeId?: number,
        orderId?: number,
    ): CancelablePromise<Array<Product>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/products/',
            query: {
                'skip': skip,
                'limit': limit,
                'product_type_id': productTypeId,
                'order_id': orderId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the job with the given product id or raise 404.
     * @param productId
     * @returns Product Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1ProductsProductIdGet(
        productId: number,
    ): CancelablePromise<Product> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/products/{product_id}',
            path: {
                'product_id': productId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Derviced Products
     * Return the list of derived products.
     * @param productId
     * @param skip
     * @param limit
     * @returns Product Successful Response
     * @throws ApiError
     */
    public static readDervicedProductsApiV1ProductsProductIdDerivedProductsGet(
        productId: number,
        skip?: any,
        limit?: any,
    ): CancelablePromise<Array<Product>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/products/{product_id}/derived-products',
            path: {
                'product_id': productId,
            },
            query: {
                'skip': skip,
                'limit': limit,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Base Products
     * Return the list of base products.
     * @param productId
     * @param skip
     * @param limit
     * @returns Product Successful Response
     * @throws ApiError
     */
    public static readBaseProductsApiV1ProductsProductIdBaseProductsGet(
        productId: number,
        skip?: any,
        limit?: any,
    ): CancelablePromise<Array<Product>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/products/{product_id}/base-products',
            path: {
                'product_id': productId,
            },
            query: {
                'skip': skip,
                'limit': limit,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of product types.
     * @param skip
     * @param limit
     * @returns ProductType Successful Response
     * @throws ApiError
     */
    public static readListApiV1ProductTypesGet(
        skip?: number,
        limit: number = 100,
    ): CancelablePromise<Array<ProductType>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/product-types/',
            query: {
                'skip': skip,
                'limit': limit,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the job with the given product type id or raise 404.
     * @param productTypeId
     * @returns ProductType Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1ProductTypesProductTypeIdGet(
        productTypeId: number,
    ): CancelablePromise<ProductType> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/product-types/{product_type_id}',
            path: {
                'product_type_id': productTypeId,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read List
     * Return the list of users.
     * @param skip
     * @param limit
     * @param xApikey
     * @returns User Successful Response
     * @throws ApiError
     */
    public static readListApiV1UsersGet(
        skip?: number,
        limit: number = 100,
        xApikey?: any,
    ): CancelablePromise<Array<User>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/users/',
            headers: {
                'x-apikey': xApikey,
            },
            query: {
                'skip': skip,
                'limit': limit,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Read Detail
     * Return the user with the given user id or raise 404.
     * @param userId
     * @param xApikey
     * @returns User Successful Response
     * @throws ApiError
     */
    public static readDetailApiV1UsersUserIdGet(
        userId: number,
        xApikey?: any,
    ): CancelablePromise<User> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/users/{user_id}',
            path: {
                'user_id': userId,
            },
            headers: {
                'x-apikey': xApikey,
            },
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Register User
     * @param requestBody
     * @returns UserSelfInformation Successful Response
     * @throws ApiError
     */
    public static registerUserApiV1UsersRegisterPost(
        requestBody: UserCredentials,
    ): CancelablePromise<UserSelfInformation> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/users/register',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                422: `Validation Error`,
            },
        });
    }

    /**
     * Login User
     * @param requestBody
     * @returns UserSelfInformation Successful Response
     * @throws ApiError
     */
    public static loginUserApiV1UsersLoginPost(
        requestBody: UserCredentials,
    ): CancelablePromise<UserSelfInformation> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/users/login',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                422: `Validation Error`,
            },
        });
    }

}
