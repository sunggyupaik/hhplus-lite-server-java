```
openapi: 3.1.0
info:
  version: '1.0'
  title: concerts
  summary: concerts-java-server
  description: 콘서트 예매 프로젝트입니다.
  termsOfService: IT
  contact:
    name: sunggyupaik
    url: www.github.com
    email: hhplus@naver.com
  license:
    url: 'https://opensource.org/licenses/MIT'
    name: MIT
servers:
  - url: 'http://localhost:8080'
    description: concerts
paths:
  /concerts/schedules:
    get:
      tags:
        - Concerts
      summary: 콘서트 날짜 목록 조회
      description: 콘서트 날짜 목록을 조회합니다.
      operationId: get-concerts-schedules
      parameters:
        - name: Authorization
          in: header
          description: '사용자 인증 수단, 액세스 토큰 값, Authorization: Bearer ${ACCESS_TOKEN}'
          schema:
            type: string
      responses:
        '200':
          $ref: '#/components/responses/schedulesResponse'
      servers:
        - url: 'http://localhost:8080'
          description: concerts
  /concerts/seats:
    get:
      tags:
        - Concerts
      summary: 콘서트 좌석 목록 조회
      description: 주어진 날짜에 콘서트 좌석 목록을 조회합니다.
      operationId: get-concerts-seats
      parameters:
        - name: Authorization
          in: header
          description: '사용자 인증 수단, 액세스 토큰 값, Authorization: Bearer ${ACCESS_TOKEN}'
          schema:
            type: string
        - name: schedule-date
          in: query
          description: 날짜
          schema:
            type: string
      responses:
        '200':
          $ref: '#/components/responses/seatsResponse'
        '400':
          $ref: '#/components/responses/commonResponse'
      servers:
        - url: 'http://localhost:8080'
          description: concerts
  /concerts/reservations:
    post:
      tags:
        - Concerts
      summary: 콘서트 좌석 예약
      description: 주어진 날짜와 좌석으로 콘서트 좌석을 예약합니다.
      operationId: post-concerts-reservations
      parameters:
        - name: Authorization
          in: header
          description: '사용자 인증 수단, 액세스 토큰 값, Authorization: Bearer ${ACCESS_TOKEN}'
          schema:
            type: string
      requestBody:
        $ref: '#/components/requestBodies/reservationRequest'
      responses:
        '200':
          $ref: '#/components/responses/reservationResponse'
        '400':
          $ref: '#/components/responses/ErrorResponse_400'
        '401':
          $ref: '#/components/responses/ErrorResponse_401'
        '409':
          $ref: '#/components/responses/ErrorResponse_409'
      servers:
        - url: 'http://localhost:8080'
          description: concerts
  /queues/token:
    post:
      tags:
        - Queues
      summary: 대기열 토큰 발급
      description: 대기열 토큰을 발급합니다.
      operationId: post-queues-token
      parameters:
        - name: Authorization
          in: header
          description: '사용자 인증 수단, 액세스 토큰 값, Authorization: Bearer ${ACCESS_TOKEN}'
          schema:
            type: string
      requestBody:
        $ref: '#/components/requestBodies/waitingTokenRequest'
      responses:
        '200':
          $ref: '#/components/responses/waitingTokenResponse'
        '401':
          $ref: '#/components/responses/ErrorResponse_401'
      servers:
        - url: 'http://localhost:8080'
          description: concerts
  /queues/status:
    get:
      tags:
        - Queues
      summary: 대기번호 조회
      description: 주어진 토큰으로 대기번호를 조회합니다.
      operationId: get-queues-status
      parameters:
        - name: Authorization
          in: header
          description: '사용자 인증 수단, 액세스 토큰 값, Authorization: Bearer ${ACCESS_TOKEN}'
          schema:
            type: string
        - name: waiting-token
          in: query
          description: 대기열 토큰
          schema:
            type: string
      responses:
        '200':
          $ref: '#/components/responses/waitingTokenStatusResponse'
        '401':
          $ref: '#/components/responses/ErrorResponse_401'
        '404':
          $ref: '#/components/responses/ErrorResponse_404'
      servers:
        - url: 'http://localhost:8080'
          description: concerts
  '/users/{userId}/points':
    get:
      tags:
        - Users
      summary: 포인트 조회
      description: 주어진 식별자로 포인트를 조회합니다.
      operationId: get-users-userId-points
      parameters:
        - name: Authorization
          in: header
          description: '사용자 인증 수단, 액세스 토큰 값, Authorization: Bearer ${ACCESS_TOKEN}'
          schema:
            type: string
        - name: userId
          in: path
          description: 사용자 식별자
          required: true
          schema:
            type: integer
      requestBody:
        content: {}
      responses:
        '200':
          $ref: '#/components/responses/UserPointResponse'
        '401':
          $ref: '#/components/responses/ErrorResponse_401'
      servers:
        - url: 'http://localhost:8080'
          description: concerts
    post:
      tags:
        - Users
      summary: 포인트 충전
      description: 주어진 식별자와 금액으로 포인트를 충전합니다
      operationId: post-users-userId-points
      parameters:
        - name: Authorization
          in: header
          description: '사용자 인증 수단, 액세스 토큰 값, Authorization: Bearer ${ACCESS_TOKEN}'
          schema:
            type: string
        - name: userId
          in: path
          description: 사용자 식별자
          required: true
          schema:
            type: integer
      requestBody:
        $ref: '#/components/requestBodies/pointChargeRequest'
      responses:
        '200':
          $ref: '#/components/responses/pointChargeResponse'
        '400':
          $ref: '#/components/responses/ErrorResponse_400'
        '401':
          $ref: '#/components/responses/ErrorResponse_401'
        '404':
          $ref: '#/components/responses/ErrorResponse_404'
        '409':
          $ref: '#/components/responses/ErrorResponse_404'
      servers:
        - url: 'http://localhost:8080'
          description: concerts
  /users/login:
    post:
      tags:
        - Users
      summary: 로그인
      description: 로그인을 검증하고 토큰을 발급합니다.
      operationId: post-users-login
      requestBody:
        $ref: '#/components/requestBodies/loginRequest'
      responses:
        '200':
          $ref: '#/components/responses/loginResponse'
        '400':
          $ref: '#/components/responses/ErrorResponse_400'
      servers:
        - url: 'http://localhost:8080'
          description: concerts
  '/reservations/{reservationId}/payment':
    post:
      tags:
        - Reservations
      summary: 예약 결제
      description: 콘서트 예약을 결제합니다
      operationId: post-reservations-reservationId-payment
      parameters:
        - name: Authorization
          in: header
          description: '사용자 인증 수단, 액세스 토큰 값, Authorization: Bearer ${ACCESS_TOKEN}'
          schema:
            type: string
        - name: reservationId
          in: path
          description: 예약 식별자
          required: true
          schema:
            type: integer
      requestBody:
        $ref: '#/components/requestBodies/paymentRequest'
      responses:
        '200':
          $ref: '#/components/responses/paymentResponse'
        '401':
          $ref: '#/components/responses/ErrorResponse_401'
        '404':
          $ref: '#/components/responses/ErrorResponse_404'
        '409':
          $ref: '#/components/responses/ErrorResponse_409'
      servers:
        - url: 'http://localhost:8080'
          description: concerts
tags:
  - name: Concerts
    description: '콘서트 조회, 예약'
  - name: Queues
    description: 대기열
  - name: Reservations
    description: 예약
  - name: Users
    description: 사용자
components:
  requestBodies:
    reservationRequest:
      content:
        application/json:
          schema:
            type: object
            properties:
              seatId:
                type: integer
                description: 좌석 식별자
              scheduleDate:
                type: string
                description: 스케쥴 일정
            required:
              - seatId
              - scheduleDate
          examples:
            Example 1:
              value:
                seatId: 1
                scheduleDate: '20250512'
    waitingTokenRequest:
      content:
        application/json:
          schema:
            type: object
            properties:
              loginToken:
                type: string
            required:
              - loginToken
          examples:
            Example 1:
              value:
                loginToken: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTYiLCJyb2xlIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzMxNTc2ODAwfQ.Xg2QJ1v7A9Zp6eEJdByZ6v3_wH0ZyYx4DgwlR2kX1Hg
    loginRequest:
      content:
        application/json:
          schema:
            type: object
            properties:
              email:
                type: string
            required:
              - email
    pointChargeRequest:
      content:
        application/json:
          schema:
            type: object
            properties:
              amount:
                type: integer
          examples:
            Example 1:
              value:
                amount: 1000
    paymentRequest:
      content:
        application/json:
          schema:
            type: object
            properties:
              reservationId:
                type: integer
              amount:
                type: integer
          examples:
            Example 1:
              value:
                reservationId: 1
                amount: 3000
  responses:
    schedulesResponse:
      description: 콘서트 날짜 목록
      content:
        application/json:
          schema:
            type: array
            items:
              type: object
              properties:
                concert_date:
                  type: string
              required:
                - concert_date
          examples:
            Example 1:
              value:
                - concert_date: '20251012'
                - concert_date: '20251013'
                - concert_date: '20251014'
    seatsResponse:
      description: 콘서트 좌석 목록
      content:
        application/json:
          schema:
            type: array
            items:
              type: object
              properties:
                seatId:
                  type: integer
                scheduleId:
                  type: integer
                seatNumber:
                  type: integer
                status:
                  type: string
              required:
                - seatId
                - scheduleId
                - seatNumber
                - status
          examples:
            Example 1:
              value:
                - seatId: 1
                  scheduleId: 1
                  seatNumber: 1
                  status: AVAILABLE
                - seatId: 2
                  scheduleId: 1
                  seatNumber: 2
                  status: RESERVED
                - seatId: 3
                  scheduleId: 1
                  seatNumber: 3
                  status: RESERVED
                - seatId: 4
                  scheduleId: 2
                  seatNumber: 1
                  status: RESERVED
                - seatId: 5
                  scheduleId: 2
                  seatNumber: 2
                  status: AVAILABLE
                - seatId: 6
                  scheduleId: 2
                  seatNumber: 3
                  status: AVAILABLE
    reservationResponse:
      description: 예약된 콘서트 좌석 정보
      content:
        application/json:
          schema:
            type: object
            properties:
              reservationId:
                type: integer
              userId:
                type: integer
              seatId:
                type: integer
              status:
                type: string
              createdAt:
                type: string
                format: date-time
            required:
              - reservationId
              - userId
              - seatId
              - status
              - createdAt
          examples:
            Example 1:
              value:
                reservationId: 1
                userId: 1
                seatId: 31
                status: RESERVED
                createdAt: '2025-11-02 19:45:32'
    waitingTokenResponse:
      description: 발급된 대기열 토큰 정보
      content:
        application/json:
          schema:
            type: object
            properties:
              waitingTokenId:
                type: integer
              userId:
                type: integer
              waitingToken:
                type: string
              createdAt:
                type: string
                format: date-time
              expiredAt:
                type: string
                format: date-time
            required:
              - waitingTokenId
              - userId
              - waitingToken
              - createdAt
              - expiredAt
          examples:
            Example 1:
              value:
                waitingTokenId: 1
                userId: 1
                status: WAITING
                waitingToken: ABCD7YQ2P9F1X3MNT8RZ
                createdAt: '2025-11-02 19:45:32'
                expiredAt: '2025-11-02 19:50:32'
    UserPointResponse:
      description: 사용자 포인트 정보
      content:
        application/json:
          schema:
            type: object
            properties:
              userId:
                type: integer
              balance:
                type: integer
            required:
              - userId
              - balance
          examples:
            Example 1:
              value:
                userId: 1
                balance: 3500
    pointChargeResponse:
      description: 충전된 사용자 포인트 정보
      content:
        application/json:
          schema:
            type: object
            properties:
              userId:
                type: integer
              balance:
                type: integer
              chargedPoint:
                type: integer
            required:
              - userId
              - balance
              - chargedPoint
          examples:
            Example 1:
              value:
                userId: 1
                balance: 2000
                chargedPoint: 1000
    loginResponse:
      description: 로그인 토큰 정보
      content:
        application/json:
          schema:
            type: object
            properties:
              userId:
                type: integer
              loginToken:
                type: string
          examples:
            Example 1:
              value:
                user_id: 10
                loginToken: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTYiLCJyb2xlIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzMxNTc2ODAwfQ.Xg2QJ1v7A9Zp6eEJdByZ6v3_wH0ZyYx4DgwlR2kX1Hg
    paymentResponse:
      description: 결제된 예약 정보
      content:
        application/json:
          schema:
            type: object
            properties:
              paymentId:
                type: integer
              userId:
                type: integer
              seatId:
                type: integer
              amount:
                type: integer
              status:
                type: string
              createdAt:
                type: string
                format: date-time
            required:
              - paymentId
              - userId
              - seatId
              - amount
              - status
              - createdAt
          examples:
            Example 1:
              value:
                paymentId: 1
                userId: 31
                seatId: 51
                amount: 35000
                status: PAID
                createdAt: '2025-11-02 19:55:32'
    waitingTokenStatusResponse:
      description: 대기번호 정보
      content:
        application/json:
          schema:
            type: object
            properties:
              waitingTokenId:
                type: integer
              userId:
                type: integer
              waitingNumber:
                type: integer
              status:
                type: string
            required:
              - waitingTokenId
              - userId
              - waitingNumber
              - status
          examples:
            Example 1:
              value:
                waitingTokenId: 1
                userId: 1
                waitingNumber: 31
                status: WAITING
    ErrorResponse_400:
      description: 요청 값이 올바르지 않은 경우
      content:
        application/json:
          schema:
            type: object
            properties:
              result:
                type: string
              data:
                type:
                  - object
                  - 'null'
              errorCode:
                type: string
              message:
                type: string
          examples:
            Example 1:
              value:
                result: FAIL
                data: null
                code: COMMON_BAD_REQUEST
                message: 요청이 올바르지 않습니다
    ErrorResponse_401:
      description: 로그인 인증이 안된 경우
      content:
        application/json:
          schema:
            type: object
            properties:
              result:
                type: string
              data:
                type:
                  - object
                  - 'null'
              errorCode:
                type: string
              message:
                type: string
          examples:
            Example 1:
              value:
                result: FAIL
                data: null
                code: COMMON_UNAUTHORIZED
                message: 인증이 올바르지 않습니다
    ErrorResponse_404:
      description: 엔티티가 존재하지 않는 경우
      content:
        application/json:
          schema:
            type: object
            properties:
              result:
                type: string
              data:
                type:
                  - object
                  - 'null'
              errorCode:
                type: string
              message:
                type: string
          examples:
            Example 1:
              value:
                result: FAIL
                data: null
                code: COMMON_ENTITY_NOT_FOUND
                message: 주어진 식별자에 해당하는 엔티티가 존재하지 않습니다
    ErrorResponse_409:
      description: 서버 자원 충돌인 경우
      content:
        application/json:
          schema:
            type: object
            properties:
              result:
                type: string
              data:
                type:
                  - object
                  - 'null'
              errorCode:
                type: string
              message:
                type: string
          examples:
            Example 1:
              value:
                result: FAIL
                data: null
                code: COMMON_CONFLICT
                message: 리소스 충돌로 서버가 요청을 처리할 수 없습니다
    commonResponse:
      description: errorResponse
      content:
        application/json:
          schema:
            type: object
            properties:
              result:
                type: string
              data:
                type:
                  - object
                  - 'null'
              errorCode:
                type: string
              message:
                type: string
          examples:
            Example 1:
              value:
                result: FAIL
                data: null
                errorCode: COMMON_BAD_REQUEST
                message: 요청한 값이 올바르지 않습니다
x-internal: false
```