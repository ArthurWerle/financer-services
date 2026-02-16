import axios, { AxiosResponse } from 'axios';

export class Service {
  constructor(private readonly baseURL: string) {
    this.baseURL = baseURL;
  }

  async get<T>(path: string, params?: any): Promise<AxiosResponse<T>> {
    return axios.get(this.baseURL + path, { params });
  }

  async delete(path: string, params?: any): Promise<AxiosResponse> {
    return axios.delete(this.baseURL + path, { params });
  }

  async post<T>(
    path: string,
    data: any,
    params?: any
  ): Promise<AxiosResponse<T>> {
    return axios.post(this.baseURL + path, data, { params });
  }

  async put(path: string, data: any): Promise<AxiosResponse> {
    return axios.put(this.baseURL + path, data);
  }
}
