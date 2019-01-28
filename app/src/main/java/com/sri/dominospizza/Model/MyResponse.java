package com.sri.dominospizza.Model;

import java.util.List;

/**
 * Created by Scarecrow on 2/6/2018.
 */

public class MyResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;
}
