package com.naruto.popmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.naruto.popmovies.R;
import com.naruto.popmovies.bean.MovieListBean;
import com.naruto.popmovies.db.model.Genre;

import org.litepal.LitePal;

import java.util.List;

/**
 * @author jelly.
 * @Date 2019-01-24.
 * @Time 14:47.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> implements View.OnClickListener {

    private List<MovieListBean.ResultsBean> mMovieList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private Cursor mCursor;

    public MovieAdapter(List<MovieListBean.ResultsBean> movieList) {
        mMovieList = movieList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_movie, parent, false);
        rootView.setOnClickListener(this);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // 获取当前条目的数据
        MovieListBean.ResultsBean movie = mMovieList.get(position);
        // 加载网络图片并设置给imageView
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.backdrop_loading)
                .error(R.mipmap.backdrop_fail_load)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.HIGH);
        Glide.with(mContext)
                .asBitmap()
                .load(movie.getBackdropPath())
                .apply(options)
                .into(new BitmapImageViewTarget(holder.ivMovieBd) {
                    @Override
                    public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                        Palette.from(resource).generate(palette -> setBackgroundColor(palette, holder));
                    }
                });

        // 设置标题
        holder.tvMovieTitle.setText(movie.getTitle());
        // 设置类型
        String genres = getGenres(movie.getGenres());
        holder.tvMovieGenre.setText(genres);
        // 设置评分数
        holder.rbMovieStar.setRating(((float) movie.getVoteAverage()) / 2);
        // 设置上映日期
        holder.tvMoviesDate.setText(movie.getReleaseDate().split("-")[0]);
        holder.cardView.setTag(position);
    }

    private void setBackgroundColor(Palette palette, ViewHolder holder) {
        holder.cardView.setCardBackgroundColor(palette.getVibrantColor(mContext.getResources().getColor(R.color.black_translucent_60)));
    }

    private String getGenres(List<Integer> genreIdList) {
        StringBuilder genres = new StringBuilder();
        for (int genreId : genreIdList) {
            Genre genre = LitePal.where("genre_id = ?", String.valueOf(genreId))
                    .find(Genre.class)
                    .get(0);
            genres.append(genre.getName()).append(" ");
        }

        return genres.toString();
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {

        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {

        if (mOnItemClickListener != null) {

            // 注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public void swapCursor(Cursor cursor) {

        if (cursor == mCursor) {

            return;
        }

        int rowIDColumn;
        boolean dataValid;
        if (cursor != null) {

            mCursor = cursor;
            rowIDColumn = mCursor.getColumnIndexOrThrow("_id");
            dataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {

            notifyItemRangeRemoved(0, getItemCount());
            mCursor = null;
            rowIDColumn = -1;
            dataValid = false;
        }
    }

    /**
     * 定义接口
     */
    public interface OnRecyclerViewItemClickListener {

        /**
         * 条目点击事件
         *
         * @param view     控件
         * @param position 位置
         */
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView ivMovieBd;
        TextView tvMovieTitle;
        RatingBar rbMovieStar;
        TextView tvMovieGenre;
        TextView tvMoviesDate;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardview);
            ivMovieBd = view.findViewById(R.id.iv_movie_bd);
            tvMovieTitle = view.findViewById(R.id.tv_title);
            rbMovieStar = view.findViewById(R.id.rb_movie_star);
            tvMovieGenre = view.findViewById(R.id.tv_movie_genre);
            tvMoviesDate = view.findViewById(R.id.tv_movies_date);
        }
    }
}
