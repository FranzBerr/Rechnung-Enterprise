import { axiosInstance } from "./axios";


// -------------------------------------------------------
//      Generic Client
// -------------------------------------------------------
export class BaseApiClient<T> {

  constructor(
    private readonly path: string
  ) {
  }

  async findAll(): Promise<T[]> {

    const response =
      await axiosInstance.get<T[]>(
        this.path
      );

    return response.data;
  }

  async findById(
    id: number
  ): Promise<T> {

    const response =
      await axiosInstance.get<T>(
        `${this.path}/${id}`
      );

    return response.data;
  }

  async create(
    dto: T
  ): Promise<T> {

    const response =
      await axiosInstance.post<T>(
        this.path,
        dto
      );

    return response.data;
  }

  async update(
    id: number,
    dto: T
  ): Promise<T> {

    const response =
      await axiosInstance.put<T>(
        `${this.path}/${id}`,
        dto
      );

    return response.data;
  }

  async delete(
    id: number
  ): Promise<void> {

    await axiosInstance.delete(
      `${this.path}/${id}`
    );
  }
}
