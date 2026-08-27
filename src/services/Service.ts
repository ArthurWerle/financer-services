import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
import type { Readable } from 'stream';

export class Service {
  constructor(private readonly baseURL: string) {
    this.baseURL = baseURL;
  }

  // POST that returns the raw upstream response as a Node stream so a route can
  // pipe it straight through to the client (used to proxy Server-Sent Events).
  // validateStatus is disabled so a non-2xx upstream (e.g. a 404 JSON body sent
  // before any SSE frame) resolves instead of throwing, letting the caller
  // forward the real status and body.
  async postStream(
    path: string,
    data: any,
    config?: AxiosRequestConfig
  ): Promise<AxiosResponse<Readable>> {
    return axios.post(this.baseURL + path, data, {
      responseType: 'stream',
      validateStatus: () => true,
      ...config,
    });
  }

  async get<T>(
    path: string,
    params?: any,
    config?: AxiosRequestConfig
  ): Promise<AxiosResponse<T>> {
    return axios.get(this.baseURL + path, { params, ...config });
  }

  async delete(
    path: string,
    params?: any,
    config?: AxiosRequestConfig
  ): Promise<AxiosResponse> {
    return axios.delete(this.baseURL + path, { params, ...config });
  }

  async post<T>(
    path: string,
    data: any,
    params?: any,
    config?: AxiosRequestConfig
  ): Promise<AxiosResponse<T>> {
    return axios.post(this.baseURL + path, data, { params, ...config });
  }

  async put(
    path: string,
    data: any,
    config?: AxiosRequestConfig
  ): Promise<AxiosResponse> {
    return axios.put(this.baseURL + path, data, config);
  }

  async patch(
    path: string,
    data: any,
    config?: AxiosRequestConfig
  ): Promise<AxiosResponse> {
    return axios.patch(this.baseURL + path, data, config);
  }
}
